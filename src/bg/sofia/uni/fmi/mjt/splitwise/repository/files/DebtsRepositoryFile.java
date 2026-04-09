package bg.sofia.uni.fmi.mjt.splitwise.repository.files;

import bg.sofia.uni.fmi.mjt.splitwise.repository.DebtsRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.Repository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers.DebtsWrapper;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DebtsRepositoryFile implements Repository, DebtsRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path path;
    private List<Debt> debts = new ArrayList<>();

    public DebtsRepositoryFile(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        if (!path.toFile().exists()) {
            save();
            return;
        }
        try {
            DebtsWrapper wrapper = objectMapper.readValue(path.toFile(), DebtsWrapper.class);
            this.debts = wrapper.debts();

        } catch (IOException e) {
            this.debts = new ArrayList<>();
        }
    }

    public void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(Files.newBufferedWriter(path), new DebtsWrapper(this.debts));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDebt(Debt debt) {
        this.debts.add(debt);
        save();
    }

    public void removeDebt(Debt debt) {
        this.debts.remove(debt);
        save();
    }

    public List<Debt> getAllDebts() {
        return this.debts;
    }

    public Debt findDebt(String from, String to) {
        return debts.stream()
                .filter(d -> d.getFrom().equals(from) && d.getTo().equals(to))
                .findFirst()
                .orElse(null);
    }

    public List<Debt> findAllDebts(String username) {
        return debts.stream()
                .filter(d -> d.getFrom().equals(username) || d.getTo().equals(username))
                .collect(Collectors.toList());
    }

}
