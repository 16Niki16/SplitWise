package bg.sofia.uni.fmi.mjt.splitwise.client.request;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.command.CommandType;

public record Request(CommandType commandType, String token, Data data) {
}
