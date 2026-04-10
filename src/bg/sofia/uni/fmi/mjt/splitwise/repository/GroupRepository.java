package bg.sofia.uni.fmi.mjt.splitwise.repository;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;

public interface GroupRepository {
    void addGroup(Group group);

    void removeGroup(String groupID);

    Group getGroup(String groupName);
}
