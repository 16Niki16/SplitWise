package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.repository.files.GroupRepository;

public class GroupService implements Service {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void addNewGroup(Group group) {
        this.groupRepository.addGroup(group);
    }

    public Group getGroupByName(String groupName) {
        return groupRepository.getGroup(groupName);
    }

    public void updateFile() {
        this.groupRepository.save();
    }
}
