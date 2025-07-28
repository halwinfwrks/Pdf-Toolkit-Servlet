package com.dev.controller.apigate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.model.Chunk;
import com.dev.model.PdfFile;
import com.dev.model.User;
import com.dev.service.ChunkService;
import com.dev.service.PdfProcessingService;
import com.dev.service.PdfService;
import com.dev.service.UserService;
import com.dev.util.JwtUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(urlPatterns = { "/api/upload" })
@MultipartConfig(fileSizeThreshold = 15 * 1024 * 1024, maxFileSize = 30 * 1024 * 1024, maxRequestSize = 50 * 1024
        * 1024)
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UploadServlet.class);

    private static final UserService userService = UserService.getInstance();
    private static final PdfService pdfService = PdfService.getInstance();
    private static final ChunkService chunkService = ChunkService.getInstance();
    private static final PdfProcessingService pdfProcessingService = PdfProcessingService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        respondWithError(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not allowed");
        logger.warn("GET request to /upload is not allowed. Redirecting to error page.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<String> usernameOpt = extractUsernameFromJwtToken(req);
        if (usernameOpt.isEmpty()) {
            respondWithError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        Optional<User> userOpt = userService.findUserByUsername(usernameOpt.get());
        if (userOpt.isEmpty()) {
            logger.error("User not found: {}", usernameOpt.get());
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        Part pdfPart = req.getPart("pdf-part");
        if (isInvalidPdfPart(pdfPart)) {
            respondWithError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid PDF file");
            return;
        }

        handleFileUpload(userOpt.get(), pdfPart, req, resp);
    }

    private Optional<String> extractUsernameFromJwtToken(HttpServletRequest req) {
        Object tokenObj = req.getHeader("Authorization");
        if (tokenObj == null) {
            logger.warn("JWT token missing in Authorization header");
            return Optional.empty();
        }
        String token = tokenObj.toString().substring(7);
        return JwtUtils.getUsernameFromJwt(token);
    }

    private boolean isInvalidPdfPart(Part pdfPart) {
        return pdfPart == null || pdfPart.getSize() == 0;
    }

    private void handleFileUpload(User user, Part pdfPart, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        File tempFile = null;

        try {
            tempFile = File.createTempFile(
                    Paths.get(pdfPart.getSubmittedFileName()).getFileName().toString(), ".pdf");
            pdfPart.write(tempFile.getAbsolutePath());

            List<Chunk> chunks = pdfProcessingService.splitPdfFileIntoChunks(tempFile.getAbsolutePath());

            PdfFile pdfFile = createPdfFile(user, pdfPart, chunks.size());
            int fileId = pdfService.savePdf(pdfFile);

            logger.info("PDF file saved with ID: {}", fileId);

            chunks.forEach(chunk -> {
                chunk.setFileId(fileId);
                chunkService.saveChunk(chunk);
            });

            logger.info("Successfully uploaded PDF: {} by user: {}", pdfFile.getName(), user.getUsername());
            responseWithSuccess(resp, "File uploaded successfully");

        } catch (Exception e) {
            logger.error("Error while handling file upload", e);
            respondWithError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing file");
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private PdfFile createPdfFile(User user, Part pdfPart, int chunkCount) {
        PdfFile pdfFile = new PdfFile();
        pdfFile.setUserId(user.getId());
        pdfFile.setName(pdfPart.getSubmittedFileName());
        pdfFile.setLastModified(new Date());
        pdfFile.setTotalChunk((long) chunkCount);
        pdfFile.setTotalSize(pdfPart.getSize());
        return pdfFile;
    }

    private void respondWithError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        logger.error("HTTP {}: {}", statusCode, message);
        resp.sendError(statusCode, message);
    }

    private void responseWithSuccess(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(message);
    }
}
