package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.currency.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;

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

public class Server {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private final CommandRegistry commandExecutor;
    private final ExchangeRate rate;

    public Server(CommandRegistry commandRegistry) {
        this.commandExecutor = commandRegistry;
        this.rate = new ExchangeRate(HttpClient.newBuilder().build());
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
                        try {
                            SocketChannel sc = (SocketChannel) key.channel();
                            readable(sc, buffer);
                        } catch (IOException e) {
                            continue;
                        }
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
        assert line != null;
        if (line.matches("\\w+\\|\\w+")) {
            creatingUser(line, buffer, sc);
        } else {
            String[] user = line.split(" ");
            clientOutput(buffer, sc, commandExecutor.execute(
                    CommandCreator.newCommand(line), users.getUser(user[USER]), rate));
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
            System.out.println("Client has closed the connection!");
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
            clientOutput(buffer, sc, Login.loginInSystem(
                    lineSplit[USER], lineSplit[PASSWORD], friends, users, tempNotifications, exception));
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
