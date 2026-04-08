package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.Request;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.CreateAccountData;
import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandRegistry;
import bg.sofia.uni.fmi.mjt.splitwise.command.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.response.ErrorResponse;
import bg.sofia.uni.fmi.mjt.splitwise.response.Response;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

@AllArgsConstructor
public class Server {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private final SessionsManager sessionsManager;
    private final CommandRegistry commandRegistry;
    private final UserService userService;

    public void start() {
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

                        buffer.clear();
                        int r = sc.read(buffer);
                        if (r < 0) {
                            System.out.println("Client has closed the connection");
                            sc.close();
                            continue;
                        }
                        buffer.flip();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        String requestMessage = new String(bytes, "UTF-8");

                        Request request = MAPPER.readValue(requestMessage, Request.class);
                        try {
                            Command command = commandRegistry.create(request);
                            String token = request.token();
                            User user;
                            if (request.data() instanceof LoginData loginData) {
                                user = userService.getUserByUsername(loginData.username());
                            } else if (request.data() instanceof CreateAccountData) {
                                user = null;
                            } else {
                                user = this.sessionsManager.getUserSession(request.token());
                            }

                            ResponseData responseData = command.execute(user);
                            Response response = new Response(token, responseData);
                            String jsonFormatting = MAPPER.writeValueAsString(response);
                            sc.write(ByteBuffer.wrap(jsonFormatting.getBytes()));
                        } catch (RuntimeException e) {
                            ResponseData responseError = ErrorResponse.of(e.getMessage());
                            Response response = new Response(request.token(), responseError);
                            String jsonFormatting = MAPPER.writeValueAsString(response);
                            sc.write(ByteBuffer.wrap(jsonFormatting.getBytes()));
                        }

                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }

                    keyIterator.remove();
                }

            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }
}
