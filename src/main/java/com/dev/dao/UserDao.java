package com.dev.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dev.model.User;

public class UserDao implements MysqlRepository<User> {

    private static UserDao instance;

    private UserDao() {
        // Private constructor to prevent instantiation
    }

    public static UserDao getInstance() {
        if (instance == null) {
            synchronized (UserDao.class) {
                if (instance == null) {
                    instance = new UserDao();
                }
            }
        }
        return instance;
    }

    @Override
    public int save(User entity) {
        String sql = "INSERT INTO users(username, password, fullname, avatar_url) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getFullname());
            preparedStatement.setString(4, entity.getAvatarUrl());
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
        }

    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User(resultSet.getInt("id"), resultSet.getString("username"),
                        resultSet.getString("password"), resultSet.getString("fullname"),
                        resultSet.getString("avatar_url"));
                users.add(user);
            }
            return users;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("id"), resultSet.getString("username"),
                        resultSet.getString("password"), resultSet.getString("fullname"),
                        resultSet.getString("avatar_url"));
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void update(User entity) {
        String sql = "UPDATE users SET username = ?, password = ?, fullname = ?, avatar_url = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getFullname());
            preparedStatement.setString(4, entity.getAvatarUrl());
            preparedStatement.setInt(5, entity.getId());
            // logger.info("{}: Updating user with ID {}", TAG, entity.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            // logger.error("{}: Error updating user with ID {}", TAG, entity.getId(), e);
        }
    }
}
