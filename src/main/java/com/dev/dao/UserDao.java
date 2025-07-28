package com.dev.dao;

import com.dev.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDao implements MysqlRepository<User> {
    private static final Logger logger = LogManager.getLogger(UserDao.class);
    private static volatile UserDao instance;

    private UserDao() {
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
    public int save(User user) {
        String sql = "INSERT INTO users(username, password, fullname, avatar_url) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getAvatarUrl());

            logger.info("Saving user: {}", user.getUsername());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No rows affected while saving user");
                return 0;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    user.setId(generatedId);
                    return generatedId;
                } else {
                    logger.warn("No ID returned after insert.");
                    return 0;
                }
            }

        } catch (Exception e) {
            logger.error("Error saving user", e);
            return 0;
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("Deleted user with ID: {}", id);

        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            logger.info("Retrieved {} users from database", users.size());
            return users;

        } catch (Exception e) {
            logger.error("Error retrieving all users", e);
            return Collections.emptyList();
        }
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (Exception e) {
            logger.error("Error finding user by ID: {}", id, e);
        }
        return null;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, fullname = ?, avatar_url = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getAvatarUrl());
            ps.setInt(5, user.getId());

            ps.executeUpdate();
            logger.info("Updated user with ID: {}", user.getId());

        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", user.getId(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("fullname"),
                rs.getString("avatar_url")
        );
    }
}
