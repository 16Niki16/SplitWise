package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.repository.GroupRepository;

public class GroupService {
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
