package bg.sofia.uni.fmi.mjt.splitwise.command.data;

import java.util.Set;

public record CreateGroupData(String groupName, Set<String> participants) {
}
