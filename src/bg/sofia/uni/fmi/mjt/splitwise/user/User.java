package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PasswordNotCorrectException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.FRIEND_LIST;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.PASSWORD;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.THREE;
import static bg.sofia.uni.fmi.mjt.splitwise.constants.Constants.USER;

public class User {
    private String username;
    private String password;
    private Map<String, Double> friendList;

    private User(String username, String password, Map<String, Double> friendList) {
        this.username = username;
        this.password = password;
        this.friendList = friendList;
    }

    public static User of(String line, String path) throws PasswordNotCorrectException {
        String[] splitedUser = line.split("\\|");
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(path, true));
             BufferedReader re = new BufferedReader(new FileReader(path))) {
            String lineR;
            boolean isUser = false;
            while ((lineR = re.readLine()) != null) {
                String[] lineRe = lineR.split("\\|");
                if (lineRe[USER].trim().equals(splitedUser[USER].trim())) {
                    isUser = true;
                    if (!lineRe[PASSWORD].trim().equals(splitedUser[PASSWORD].trim())) {
                        throw new PasswordNotCorrectException("Entered password not correct");
                    } else if (lineRe.length == THREE) {
                        return new User(splitedUser[USER], splitedUser[PASSWORD],
                            extractFriends(lineRe[FRIEND_LIST]));
                    }
                    break;
                }
            }
            if (!isUser) {
                wr.write(line);
                wr.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new User(splitedUser[USER], splitedUser[PASSWORD], new LinkedHashMap<>());
    }

    private static Map<String, Double> extractFriends(String friends) {
        String[] splitedFriends = friends.split(",");
        Map<String, Double> friendsOwes = new LinkedHashMap<>();
        for (String spl : splitedFriends) {
            String[] mapItem = spl.trim().split(" ");
            friendsOwes.put(mapItem[USER], Double.valueOf(mapItem[1]));
        }
        return friendsOwes;
    }
}
