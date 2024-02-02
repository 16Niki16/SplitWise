package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;
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
    private static final String DIRECTORY = "DataFiles\\UserData.txt";
    private Set<User> users;
    private CommandExecutor commandExecutor;

    public Server(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        this.users = new LinkedHashSet<>();
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
        buffer.clear(); // switch to writing mode
        int r = sc.read(buffer); // buffer fill
        if (r < 0) {
            System.out.println("Client has closed the connection");
            sc.close();
            return null;
        }
        buffer.flip(); // switch to reading mode

        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);

        return new String(byteArray, "UTF-8"); // buffer drain
    }

    private void creatingUser(String line, ByteBuffer buffer, SocketChannel sc) throws IOException {
        try {
            User userSession = User.of(line, DIRECTORY);
            this.users.add(userSession);
            String[] lineSplit = line.split(" ");
            clientOutput(buffer, sc, String.format("Welcome %s!", lineSplit[0]));

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
