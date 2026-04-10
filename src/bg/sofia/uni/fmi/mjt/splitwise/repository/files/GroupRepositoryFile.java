package bg.sofia.uni.fmi.mjt.splitwise.repository.files;

import bg.sofia.uni.fmi.mjt.splitwise.containers.Group;
import bg.sofia.uni.fmi.mjt.splitwise.repository.GroupRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.Repository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers.GroupWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class GroupRepositoryFile implements Repository, GroupRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path path;
    private Map<String, Group> groups = new HashMap<>();

    public GroupRepositoryFile(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        if (!path.toFile().exists()) {
            save();
            return;
        }
        try {
            GroupWrapper wrapper = objectMapper.readValue(path.toFile(), GroupWrapper.class);
            this.groups = wrapper.groups();

        } catch (IOException e) {
            this.groups = new HashMap<>();
        }
    }

    private void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(Files.newBufferedWriter(path), new GroupWrapper(this.groups));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Group getGroup(String groupName) {
        return this.groups.get(groupName);
    }

    @Override
    public void addGroup(Group group) {
        this.groups.put(group.getGroupName(), group);
        save();
    }

    @Override
    public void removeGroup(String groupID) {
        this.groups.remove(groupID);
        save();
    }

    public Map<String, Group> getAllGroups() {
        return this.groups;
    }
}
