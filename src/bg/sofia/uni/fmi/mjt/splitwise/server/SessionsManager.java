package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.containers.User;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonAlreadyLoggedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SessionNotActiveException;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class SessionsManager {
    private Map<String, User> sessions = new HashMap<>();

    public String createSession(User user) {
        if (sessions.containsValue(user)) {
            throw new PersonAlreadyLoggedException("The provided user is already logged!");
        }

        String uniqueSessionID = UUID.randomUUID().toString();
        this.sessions.put(uniqueSessionID, user);
        return uniqueSessionID;
    }

    public void removeSession(String uniqueSessionID) {
        if (!sessions.containsKey(uniqueSessionID)) {
            throw new SessionNotActiveException("There is not an active session using the provided token!");
        }
        this.sessions.remove(uniqueSessionID);
    }
}
