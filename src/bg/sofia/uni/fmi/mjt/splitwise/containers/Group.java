package bg.sofia.uni.fmi.mjt.splitwise.containers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter

public class Group {
    private final String groupName;
    private final String creator;
    private Set<String> participants = new HashSet<>();

    @JsonCreator
    public Group(
        @JsonProperty("groupName") String groupName,
        @JsonProperty("creator") String creator,
        @JsonProperty("participants") Set<String> participants
    ) {
        this.groupName = groupName;
        this.creator = creator;
        this.participants = participants != null ? participants : new HashSet<>();
    }
}
