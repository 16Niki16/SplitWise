package bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;

import java.util.Map;

public record GroupWrapper(Map<String, Group> groups) {
}
