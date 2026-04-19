package bg.sofia.uni.fmi.mjt.splitwise.server;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
