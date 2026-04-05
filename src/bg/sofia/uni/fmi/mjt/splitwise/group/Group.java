package bg.sofia.uni.fmi.mjt.splitwise.group;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter

public class Group {
    private final String groupName;
    private final String creator;
    private final Set<String> participants = new HashSet<>();

    public Group(String groupName, String creator, Set<String> participants) {
        this.groupName = groupName;
        this.creator = creator;
        this.participants.add(creator);
        this.participants.addAll(participants);
    }
}
