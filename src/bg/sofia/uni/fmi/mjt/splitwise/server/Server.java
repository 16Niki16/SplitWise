package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.Helpers;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.HelpersNotifications;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.BUFFER_SIZE;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SERVER_HOST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.SERVER_PORT;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class Server {
    private Set<User> users;
    private CommandExecutor commandExecutor;
    private ReaderWriterCreator friends;
    private ReaderWriterCreator tempNotifications;
    private ReaderWriterCreator exception;

    public Server(CommandExecutor commandExecutor, String friends, String tempNotifications, String exception) {
        this.commandExecutor = commandExecutor;
        this.users = new LinkedHashSet<>();
        this.friends = new ReaderWriterCreator(friends);
        this.tempNotifications = new ReaderWriterCreator(tempNotifications);
        this.exception = new ReaderWriterCreator(exception);
    }

    public void serverStart() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

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
                        SocketChannel sc = (SocketChannel) key.channel();
                        readable(sc, buffer);
                    } else if (key.isAcceptable()) {
                        acceptable(key, selector);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    private void readable(SocketChannel sc, ByteBuffer buffer) throws IOException {
        String line = clientInput(buffer, sc);
        if (line == null) {
        } else if (line.contains("|")) {
            creatingUser(line, buffer, sc);
        } else {
            String[] user = line.split(" ");
            String commandResult =
                commandExecutor.execute(CommandCreator.newCommand(line), getUser(user[USER]));
            clientOutput(buffer, sc, commandResult);
        }
    }

    private User getUser(String username) {
        return users.stream()
            .filter(p -> p.getUsername().trim().equals(username.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Mistake in files for user"));
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
            String[] lineSplit = line.split(" ");
            boolean inFile = Helpers.checkInFileNoException(lineSplit[USER].trim(), friends);
            User userSession = User.of(line, friends);
            this.users.add(userSession);
            if (!inFile) {
                clientOutput(buffer, sc, String.format("Welcome %s!", lineSplit[USER].trim()));
            } else {
                clientOutput(buffer, sc,
                    String.format("Welcome %s!\n%s", lineSplit[USER].trim(),
                        HelpersNotifications.getNotifications(lineSplit[USER].trim(), tempNotifications, exception)));
            }
        } catch (PasswordNotCorrectException e) {
            String messageWrongPassword = "Entered wrong password";
            clientOutput(buffer, sc, messageWrongPassword);
        }
    }

    private void acceptable(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }
}
