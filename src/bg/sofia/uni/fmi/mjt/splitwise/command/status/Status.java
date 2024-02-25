package bg.sofia.uni.fmi.mjt.splitwise.command.status;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.currency.client.ExchangeRate;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotCorrectQueryException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UnknownCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.helpers.ExceptionFormater;
import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Status implements StatusAPI {
    private final ReaderWriterCreator groupsDirectory;
    private final ReaderWriterCreator exceptions;
    private final User user;
    private final ExchangeRate rate;

    public Status(ReaderWriterCreator groupsDirectory, ReaderWriterCreator exceptionsDirectory,
                  User user, ExchangeRate rate) {
        this.groupsDirectory = groupsDirectory;
        this.exceptions = exceptionsDirectory;
        this.user = user;
        this.rate = rate;
    }

    public String getStatus(Command command) {
        try {
            return peopleOwes() + groupAppend(command);

        } catch (NotCorrectQueryException | UnknownCurrencyException e) {
            ExceptionFormater.exceptionAdd(user.getUsername(), e.getLocalizedMessage(), e.getStackTrace(), exceptions);
            return e.getLocalizedMessage();
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

    private String groupAppend(Command command) throws NotCorrectQueryException,
            URISyntaxException, UnknownCurrencyException {
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
            ExceptionFormater.exceptionAdd(command.line(), "Could not extract in status groups", e.getStackTrace(),
                    exceptions);
            throw new RuntimeException("Could not extract groups. IO", e);
        }
    }
}
