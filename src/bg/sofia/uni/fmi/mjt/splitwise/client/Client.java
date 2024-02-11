package bg.sofia.uni.fmi.mjt.splitwise.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    private static final int STARTER = 1;
    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 512;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private boolean isLogged;
    private String usernameTrack;

    public Client() {
        this.isLogged = false;
    }

    public void serverConnect() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            String message;
            while (true) {
                if (!isLogged) {
                    message = isNotLogged(scanner);
                    if (disconnect(message)) {
                        break;
                    }
                } else {
                    String usernameCommand = command(scanner);
                    message = usernameTrack + " " + usernameCommand;
                    if (disconnect(usernameCommand)) {
                        break;
                    }
                }
                clientInput(buffer, socketChannel, message);
                String reply = serverOutput(buffer, socketChannel);
                if (!isLogged && !reply.equals("Entered wrong password")) {
                    isLogged = true;
                    usernameTrack = usernameExtract(reply);
                }
                System.out.println(reply);
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private String serverOutput(ByteBuffer buffer, SocketChannel sc) throws IOException {
        buffer.clear();
        sc.read(buffer);
        buffer.flip();

        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return new String(byteArray, "UTF-8");
    }

    private void clientInput(ByteBuffer buffer, SocketChannel sc, String line) throws IOException {
        buffer.clear();
        buffer.put(line.getBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private boolean disconnect(String command) {
        return command.equals("disconnect");
    }

    private String usernameExtract(String response) {
        StringBuilder builder = new StringBuilder();
        String[] splitt = response.split("\n");
        String[] splitedRes = splitt[0].split(" ");
        for (int i = STARTER; i < splitedRes.length; i++) {
            builder.append(splitedRes[i]).append(" ");
        }
        return builder.substring(0, builder.length() - 2);
    }

    private String isNotLogged(Scanner scanner) {
        System.out.print("Enter username: ");
        String usernameCommand = scanner.nextLine();
        if (disconnect(usernameCommand)) {
            return usernameCommand;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return usernameCommand + "|" + password;
    }

    private String command(Scanner scanner) {
        System.out.print("Enter command: ");
        return scanner.nextLine();
    }
}
