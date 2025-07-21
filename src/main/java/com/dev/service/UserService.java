package com.dev.service;

import java.util.List;
import java.util.Optional;

import com.dev.dao.UserDao;
import com.dev.model.User;

public class UserService {
    private static UserDao userDao = UserDao.getInstance();

    private static UserService instance;

    private UserService() {
    }

    public int saveUser(User user) {
        return userDao.save(user);
    }

    public void deleteUser(int id) {
        userDao.delete(id);
    }

    public Optional<User> findUserById(int id) {
        return Optional.ofNullable(userDao.findById(id));
    }

    public void updateUser(User user) {
        userDao.update(user);
    }

    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public boolean isUsernameExists(String username) {
        return userDao.findAll().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    public Optional<User> authenticate(String username, String password) {
        List<User> users = userDao.findAll();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();

    }

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

}
