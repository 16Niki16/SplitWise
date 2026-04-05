package bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers;

import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.util.Map;

public record UserWrapper(Map<String, User> users) {
}
