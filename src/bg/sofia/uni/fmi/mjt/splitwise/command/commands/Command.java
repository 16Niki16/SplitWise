package bg.sofia.uni.fmi.mjt.splitwise.command.commands;

import bg.sofia.uni.fmi.mjt.splitwise.client.request.dto.Data;
import bg.sofia.uni.fmi.mjt.splitwise.response.ResponseData;

public interface Command<T extends Data> {
    ResponseData execute(String token, T data);
}
