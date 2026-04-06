package bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;

import java.util.Map;

public record GroupWrapper(Map<String, Group> groups) {
}
