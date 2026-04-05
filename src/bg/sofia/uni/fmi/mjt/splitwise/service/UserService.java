package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.PersonNotRegisteredException;
import bg.sofia.uni.fmi.mjt.splitwise.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        User user = userRepository.getUser(username);

        if (user == null) {
            throw new PersonNotRegisteredException("The provided person is not registered yet!");
        }
        return user;
    }

    public void addUser(User user) {
        this.userRepository.addUser(user);
    }

    public void updateFile() {
        this.userRepository.save();
    }
}
