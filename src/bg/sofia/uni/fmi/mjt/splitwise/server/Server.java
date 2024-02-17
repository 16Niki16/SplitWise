package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.containers.ClientContainer;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.login.Login;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PASSWORD;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class Server {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private ClientContainer users;
    private CommandExecutor commandExecutor;
    private ReaderWriterCreator friends;
    private ReaderWriterCreator tempNotifications;
    private ReaderWriterCreator exception;
    private HttpClient httpClient;

    public Server(CommandExecutor commandExecutor, String friends, String tempNotifications, String exception) {
        this.commandExecutor = commandExecutor;
        this.friends = new ReaderWriterCreator(friends);
        this.tempNotifications = new ReaderWriterCreator(tempNotifications);
        this.exception = new ReaderWriterCreator(exception);
        this.users = new ClientContainer(this.friends);
        this.httpClient = HttpClient.newBuilder().build();
    }

    public void serverStart() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            users.connectUserAtStart(friends);

            while (true) {
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isReadable()) {
                        try {
                            SocketChannel sc = (SocketChannel) key.channel();
                            readable(sc, buffer);
                        } catch (IOException e) {
                            ExceptionFormater.exceptionAdd("server connection", "connection closed from client",
                                e.getStackTrace(), exception);
                            continue;
                        }
                    } else if (key.isAcceptable()) {
                        acceptable(key, selector);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            ExceptionFormater.exceptionAdd("server", "There is a problem with the server socket", e.getStackTrace(),
                exception);
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    private void readable(SocketChannel sc, ByteBuffer buffer) throws IOException {
        String line = clientInput(buffer, sc);
        if (line == null) {
        } else if (line.matches("\\w+\\|\\w+")) {
            creatingUser(line, buffer, sc);
        } else {
            String[] user = line.split(" ");
            String commandResult =
                commandExecutor.execute(CommandCreator.newCommand(line), users.getUser(user[USER]), users, httpClient);
            clientOutput(buffer, sc, commandResult);
        }
    }

    private void clientOutput(ByteBuffer buffer, SocketChannel sc, String line) throws IOException {
        buffer.clear();
        buffer.put(line.getBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private String clientInput(ByteBuffer buffer, SocketChannel sc) throws IOException {
        buffer.clear();
        int r = sc.read(buffer);
        if (r < 0) {
            System.out.println("Client has closed the connection");
            sc.close();
            return null;
        }
        buffer.flip();

        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);

        return new String(byteArray, "UTF-8");
    }

    private void creatingUser(String line, ByteBuffer buffer, SocketChannel sc) throws IOException {
        try {
            String[] lineSplit = line.split("\\|");
            boolean inFile = Helpers.checkInFileNoException(lineSplit[USER], friends);
            this.users.addUser(
                Login.loginInSystem(lineSplit[USER], lineSplit[PASSWORD], friends, users));
            if (!inFile) {
                clientOutput(buffer, sc, String.format("Welcome %s!", lineSplit[USER]));
            } else {
                clientOutput(buffer, sc,
                    String.format("Welcome %s!\n%s", lineSplit[USER],
                        HelpersNotifications.getNotifications(lineSplit[USER], tempNotifications, exception)));
            }
        } catch (PasswordNotCorrectException e) {
            clientOutput(buffer, sc, e.getLocalizedMessage());
        }
    }

    private void acceptable(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }
}
