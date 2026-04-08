package bg.sofia.uni.fmi.mjt.splitwise.client.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;

public record Request(String command, String token, Data data) {
}
