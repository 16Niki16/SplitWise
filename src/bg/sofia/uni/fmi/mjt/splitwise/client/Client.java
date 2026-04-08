package bg.sofia.uni.fmi.mjt.splitwise.client;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.DataCreator;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandType;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.splitwise.client.request.CommandLineSeparator.commandLineSeparated;

public class Client {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 512;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final DataCreator dataCreator = new DataCreator();
    private String sessionToken = null;

    public void clientStart() {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();
                CommandLine commandLine = commandLineSeparated(message);
                Data data = dataCreator.createData(commandLine);
                Request request = new Request(CommandType.of(commandLine.line()), sessionToken, data);
                String jsonFormatting = MAPPER.writeValueAsString(request);
                if ("quit".equals(message)) {
                    break;
                }

                buffer.clear();
                buffer.put(jsonFormatting.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String responseMessage = new String(byteArray, "UTF-8");
                Response response = MAPPER.readValue(responseMessage, Response.class);
                this.sessionToken = response.token();

                System.out.println(response.responseData().getResponse());
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
