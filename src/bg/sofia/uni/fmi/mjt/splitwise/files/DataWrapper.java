package bg.sofia.uni.fmi.mjt.splitwise.files;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.util.Map;

public record DataWrapper(Map<String, User> users, Map<String, Group> groups) {
}
