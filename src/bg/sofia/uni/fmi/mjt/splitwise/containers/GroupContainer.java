package bg.sofia.uni.fmi.mjt.splitwise.containers;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GroupContainer {
    private static final int STARTING_CAPACITY = 0;
    private static final int MAX_CAPACITY = 50;
    Set<Group> groups;

    public GroupContainer(ReaderWriterCreator groupsDirectory) {
        try {
            groups = connectGroupsAtStart(groupsDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Group> getAllGroups() {
        return this.groups;
    }

    public Group getUser(String name) {
        return groups.stream()
            .filter(p -> p.getGroupName().equals(name.strip()))
            .findAny()
            .orElse(null);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public Set<Group> connectGroupsAtStart(ReaderWriterCreator groupsDirectory) throws IOException {
        int capacity = STARTING_CAPACITY;
        Set<Group> group = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(groupsDirectory.getRead())) {
            String line;
            while ((line = reader.readLine()) != null && capacity != MAX_CAPACITY) {
                group.add(Group.ofSplit(line));
                ++capacity;
            }
            return group;
        }
    }
}
