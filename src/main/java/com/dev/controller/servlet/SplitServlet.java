package com.dev.controller.servlet;

import java.util.List;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.service.ExecuteFileService;
import com.dev.service.SplitPdfFileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/split")
public class SplitServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(SplitServlet.class);
    private static final long serialVersionUID = 1L;

    private static final SplitPdfFileService splitPdfFileService = SplitPdfFileService.getInstance();
    private static final ExecuteFileService executeFileService = ExecuteFileService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileIdParam = req.getParameter("fileId");
        if (fileIdParam == null || fileIdParam.isBlank()) {
            logger.error("Missing or blank fileId");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid fileId");
            return;
        }

        int fileId;
        try {
            fileId = Integer.parseInt(fileIdParam);
        } catch (NumberFormatException e) {
            logger.error("Invalid fileId format: {}", fileIdParam, e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid fileId format");
            return;
        }

        if (fileId <= 0) {
            logger.error("Invalid file ID: {}", fileId);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file ID");
            return;
        }

        List<String> imgs = splitPdfFileService
                .splitPdfIntoImages(executeFileService.mergeChunksToPdfFile(fileId).get());

        req.setAttribute("imgs", imgs);
        req.getRequestDispatcher("/split.jsp").forward(req, resp);
    }

}
