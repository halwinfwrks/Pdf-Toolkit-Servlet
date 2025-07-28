package com.dev.controller.servlet;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.model.User;
import com.dev.service.MergePdfFileService;
import com.dev.service.PdfProcessingService;
import com.dev.util.SessionUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/merge")
public class MergeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UploadServlet.class);

    private static final MergePdfFileService mergePdfFileService = MergePdfFileService.getInstance();
    private static final PdfProcessingService pdfProcessingService = PdfProcessingService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = SessionUtils.extractUserFromSession(req).orElse(null);
        if (user == null) {
            logger.warn("Unauthorized access attempt to merge servlet.");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated.");
            return;
        }
        int file1 = req.getAttribute("file1") != null ? (int) req.getAttribute("file1") : 11;
        int file2 = req.getAttribute("file2") != null ? (int) req.getAttribute("file2") : 13;

        int fileId = mergePdfFileService.mergePdfs(user, file1, file2);

        List<String> imgs = pdfProcessingService.loadPdfIntoImages(fileId);
        req.setAttribute("images", imgs);
        req.getRequestDispatcher("/merge.jsp").forward(req, resp);
    }

}
