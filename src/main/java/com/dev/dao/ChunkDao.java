package com.dev.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.model.Chunk;

public class ChunkDao implements MysqlRepository<Chunk> {
    private static final Logger logger = LogManager.getLogger(ChunkDao.class);
    private static volatile ChunkDao instance;

    private ChunkDao() {
    }

    public static ChunkDao getInstance() {
        if (instance == null) {
            synchronized (ChunkDao.class) {
                if (instance == null) {
                    instance = new ChunkDao();
                }
            }
        }
        return instance;
    }

    @Override
    public int save(Chunk entity) {
        String sql = "INSERT INTO chunks(file_id, chunk_index, chunk_data) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getFileId());
            pstmt.setInt(2, entity.getIndex());
            pstmt.setString(3, entity.getValue());

            int affectedRows = pstmt.executeUpdate();
            logger.info("Chunk saved: {}, affectedRows={}", entity, affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            logger.error("Error saving chunk: {}", entity, e);
            return 0;
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM chunks WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            logger.info("Deleted chunk with id: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting chunk with id: {}", id, e);
        }
    }

    @Override
    public List<Chunk> findAll() {
        String sql = "SELECT * FROM chunks";
        List<Chunk> chunks = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                chunks.add(mapResultSetToChunk(rs));
            }
            return chunks;
        } catch (SQLException e) {
            logger.error("Error retrieving all chunks", e);
            return Collections.emptyList();
        }
    }

    public List<Chunk> findByFileId(int fileId) {
        String sql = "SELECT * FROM chunks WHERE file_id = ?";
        List<Chunk> chunks = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    chunks.add(mapResultSetToChunk(rs));
                }
            }
            return chunks;
        } catch (SQLException e) {
            logger.error("Error retrieving chunks by file_id: {}", fileId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Chunk findById(int id) {
        String sql = "SELECT * FROM chunks WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChunk(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving chunk by id: {}", id, e);
        }
        return null;
    }

    @Override
    public void update(Chunk entity) {
        String sql = "UPDATE chunks SET file_id = ?, chunk_index = ?, chunk_data = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getFileId());
            pstmt.setInt(2, entity.getIndex());
            pstmt.setString(3, entity.getValue());
            pstmt.setInt(4, entity.getId());

            int affected = pstmt.executeUpdate();
            logger.info("Updated chunk: {}, affectedRows={}", entity, affected);
        } catch (SQLException e) {
            logger.error("Error updating chunk: {}", entity, e);
        }
    }

    private Chunk mapResultSetToChunk(ResultSet rs) throws SQLException {
        return new Chunk(
                rs.getInt("id"),
                rs.getInt("file_id"),
                rs.getInt("chunk_index"),
                rs.getString("chunk_data")
        );
    }
}
