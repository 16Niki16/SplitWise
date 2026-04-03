package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.CommandLine;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Status implements StatusAPI {
    private final ReaderWriterCreator groupsDirectory;
    private final User user;
    private final ExchangeRate rate;

    public Status(ReaderWriterCreator groupsDirectory, User user, ExchangeRate rate) {
        this.groupsDirectory = groupsDirectory;
        this.user = user;
        this.rate = rate;
    }

    public String getStatus(CommandLine command) {
        try {
            return peopleOwes() + groupAppend(command);
        } catch (URISyntaxException e) {
            throw new RuntimeException("IO exceptions", e);
        }
    }

    private String peopleOwes() {
        StringBuilder build = new StringBuilder("Friend list:\n");
        String status = user.getStatus();

        if (status.isEmpty()) {
            return "You do not have debts with friends!";
        }

        return build.append(status).toString();
    }

    private String groupAppend(CommandLine command) throws URISyntaxException {
        try (BufferedReader r = new BufferedReader(groupsDirectory.getRead())) {

            StringBuilder build = new StringBuilder("Groups:\n");
            String line;

            Map<String, Double> currencies = new HashMap<>();
            if (!user.getCurrency().equalsIgnoreCase("bgn")) {
                currencies = rate.exchange(user.getCurrency(), "bgn");
            }

            while ((line = r.readLine()) != null) {
                Group checkGroup = Group.ofSplit(line);
                if (checkGroup.checkPersonContains(user.getUsername())) {
                    build.append(checkGroup.addOwes(user, currencies));
                }
            }

            return (build.toString().equals("Groups:\n")) ? "You do not have debts in the groups!" : build.toString();
        } catch (IOException e) {

            throw new RuntimeException("Could not extract groups. IO", e);
        }
    }
}
