package crypto_mail.service.user;

import crypto_mail.model.User;
import crypto_mail.service.util.MD5Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private static final String USERS_FILE_PATH = "./config/users";

    public UserService() {}

    public Boolean addUser(String name, String password) {
        List<User> users = readUsers();

        if(users.stream().anyMatch(user -> user.getName().equals(name)))
            return false;

        users.add(new User(name, password));
        writeUsers(users);
        return true;
    }

    public void updateUser(User user) {
        List<User> users = readUsers();
        users.addAll(users.stream()
                .filter(u -> !u.getName().equals(user.getName()))
                .collect(Collectors.toList()));
        users.add(user);
        writeUsers(users);
    }

    public Boolean checkCredentionals(String name, String password) {
        return readUsers().stream()
                .anyMatch(user -> user.getName().equals(name) &&
                            user.getPassword().equals(MD5Util.md5Hash(password)));
    }

    private List<User> readUsers() {
        List<User> users = new ArrayList<>();
        File usersFile = new File(USERS_FILE_PATH);

        try {
            ObjectInputStream objectUsersStream = new ObjectInputStream(new FileInputStream(usersFile));
            User user = (User) objectUsersStream.readObject();
            while (user != null) {
                users.add(user);
                user = (User) objectUsersStream.readObject();
            }
            objectUsersStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void writeUsers(List<User> users) {
        File usersFile = new File(USERS_FILE_PATH);

        try {
            ObjectOutputStream objectUsersStream = new ObjectOutputStream(new FileOutputStream(usersFile));
            for (User user : users) {
                objectUsersStream.write(user.toString().getBytes());
            }
            objectUsersStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
