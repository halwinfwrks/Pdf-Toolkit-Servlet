package com.dev.dao;

import com.dev.model.PdfFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfDao implements MysqlRepository<PdfFile> {
    private static final Logger logger = LogManager.getLogger(PdfDao.class);
    private static volatile PdfDao instance;

    private PdfDao() {
    }

    public static PdfDao getInstance() {
        if (instance == null) {
            synchronized (PdfDao.class) {
                if (instance == null) {
                    instance = new PdfDao();
                }
            }
        }
        return instance;
    }

    // =============================== INSERT ===============================
    @Override
    public int save(PdfFile pdf) {
        final String sql = "INSERT INTO pdfs(user_id, name, total_size, total_chunk, last_modified) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pdf.getUserId());
            stmt.setString(2, pdf.getName());
            stmt.setLong(3, pdf.getTotalSize());
            stmt.setLong(4, pdf.getTotalChunk());
            stmt.setDate(5, new java.sql.Date(pdf.getLastModified().getTime()));

            logger.info("Saving PDF file: {}", pdf.getName());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No rows affected while inserting PDF.");
                return 0;
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    pdf.setId(id);
                    return id;
                } else {
                    logger.warn("Insert succeeded but no ID returned.");
                    return 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Error saving PDF file", e);
            return 0;
        }
    }

    // =============================== DELETE ===============================
    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM pdfs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error deleting PDF file with id: {}", id, e);
        }
    }

    // =============================== FIND BY ID ===============================
    @Override
    public PdfFile findById(int id) {
        final String sql = "SELECT * FROM pdfs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPdfFile(rs);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding PDF by ID: {}", id, e);
        }
        return null;
    }

    // =============================== UPDATE ===============================
    @Override
    public void update(PdfFile pdf) {
        final String sql = "UPDATE pdfs SET user_id = ?, name = ?, total_size = ?, total_chunk = ?, last_modified = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pdf.getUserId());
            stmt.setString(2, pdf.getName());
            stmt.setLong(3, pdf.getTotalSize());
            stmt.setLong(4, pdf.getTotalChunk());
            stmt.setDate(5, new java.sql.Date(pdf.getLastModified().getTime()));
            stmt.setInt(6, pdf.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error updating PDF file with id: {}", pdf.getId(), e);
        }
    }

    // =============================== FIND ALL ===============================
    @Override
    public List<PdfFile> findAll() {
        final String sql = "SELECT * FROM pdfs";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            List<PdfFile> pdfList = new ArrayList<>();
            while (rs.next()) {
                pdfList.add(mapResultSetToPdfFile(rs));
            }
            return pdfList;

        } catch (SQLException e) {
            logger.error("Error fetching all PDF files", e);
            return Collections.emptyList();
        }
    }

    // =============================== UTIL ===============================
    private PdfFile mapResultSetToPdfFile(ResultSet rs) throws SQLException {
        return new PdfFile(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getLong("total_size"),
                rs.getLong("total_chunk"),
                rs.getDate("last_modified"));
    }
}
