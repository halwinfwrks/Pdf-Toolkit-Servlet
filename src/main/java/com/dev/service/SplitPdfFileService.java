package com.dev.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.dev.dao.ChunkDao;
import com.dev.util.RenderUltils;

public class SplitPdfFileService {
    private static final Logger logger = LogManager.getLogger(ChunkDao.class);
    private static volatile SplitPdfFileService instance;

    private final RenderUltils renderUltils = RenderUltils.getInstance();

    private SplitPdfFileService() {
    }

    public static SplitPdfFileService getInstance() {
        if (instance == null) {
            synchronized (SplitPdfFileService.class) {
                if (instance == null) {
                    instance = new SplitPdfFileService();
                }
            }
        }
        return instance;
    }

    public List<String> splitPdfIntoImages(PDDocument document) {
        List<String> result = Collections.synchronizedList(new ArrayList<>());
        PDFRenderer renderer = new PDFRenderer(document);

        // Tạo thread pool
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<String>> futures = new ArrayList<>();

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            final int currentPage = page;
            Future<String> future = executor.submit(() -> {
                try {
                    BufferedImage image = renderer.renderImageWithDPI(currentPage, 150f, ImageType.RGB);
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ImageIO.write(image, "jpeg", baos);
                        byte[] imageBytes = baos.toByteArray();
                        return renderUltils.encodeToBase64(imageBytes);
                    }
                } catch (IOException e) {
                    logger.error("Error processing page {}: {}", currentPage, e.getMessage(), e);
                    return null;
                }
            });
            futures.add(future);
        }

        // Thu thập kết quả
        for (Future<String> future : futures) {
            try {
                String base64Image = future.get();
                if (base64Image != null) {
                    result.add(base64Image);
                }
            } catch (Exception e) {
                logger.error("Error getting future result: {}", e.getMessage(), e);
            }
        }

        executor.shutdown();
        return result;
    }

}