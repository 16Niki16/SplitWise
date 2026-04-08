package bg.sofia.uni.fmi.mjt.splitwise.request;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;

public record AuthenticationResult(String token, User user) {
}
