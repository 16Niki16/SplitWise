package bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;

import java.util.Map;

public record UserWrapper(Map<String, User> users) {
}
