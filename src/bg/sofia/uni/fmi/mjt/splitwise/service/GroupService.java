package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.files.GroupRepositoryFile;

public class GroupService implements Service {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepositoryFile groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void addNewGroup(Group group) {
        this.groupRepository.addGroup(group);
    }

    public Group getGroupByName(String groupName) {
        return groupRepository.getGroup(groupName);
    }
}
