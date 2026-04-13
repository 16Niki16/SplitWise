package bg.sofia.uni.fmi.mjt.splitwise.repository.files;

import bg.sofia.uni.fmi.mjt.splitwise.repository.DebtsRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.wrappers.DebtsWrapper;
import bg.sofia.uni.fmi.mjt.splitwise.containers.Debt;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DebtsRepositoryFile implements DebtsRepository {
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

    private void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(Files.newBufferedWriter(path), new DebtsWrapper(this.debts));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDebt(Debt debt) {
        this.debts.add(debt);
        save();
    }

    @Override
    public void removeDebt(Debt debt) {
        this.debts.remove(debt);
        save();
    }

    @Override
    public List<Debt> getAllDebts() {
        return this.debts;
    }

    @Override
    public Debt findDebt(String from, String to) {
        return debts.stream()
            .filter(d -> d.getFrom().equals(from) && d.getTo().equals(to))
            .findFirst()
            .orElse(null);
    }

}
