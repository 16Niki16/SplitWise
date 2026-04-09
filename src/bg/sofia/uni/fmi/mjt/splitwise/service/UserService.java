package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.repository.files.UserRepositoryFile;
import bg.sofia.uni.fmi.mjt.splitwise.containers.User;

public class UserService implements Service {

    private final UserRepository userRepository;

    public UserService(UserRepositoryFile userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        User user = userRepository.getUser(username);

        if (user == null) {
            throw new PersonNotRegisteredException("The provided person is not registered yet!");
        }
        return user;
    }

    public boolean checkUserExists(String username) {
        return userRepository.getUser(username) != null;
    }

    public void addUser(User user) {
        this.userRepository.addUser(user);
    }
}
