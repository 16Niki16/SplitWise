package bg.sofia.uni.fmi.mjt.splitwise.client.request.dto;

import java.util.Set;

public record CreateGroupData(String groupName, Set<String> participants) implements Data {
}
