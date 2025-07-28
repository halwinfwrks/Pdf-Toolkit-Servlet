package com.dev.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.dev.model.Chunk;
import com.dev.model.PdfFile;
import com.dev.model.User;
import com.dev.util.RenderUltils;

import jakarta.servlet.http.Part;

public class PdfProcessingService {

    private static final Logger logger = LogManager.getLogger(PdfProcessingService.class);
    private static final int CHUNK_SIZE = 2 * 1024 * 1024; // 2 MB

    private static PdfProcessingService instance;

    private final ChunkService chunkService = ChunkService.getInstance();
    private final PdfService pdfService = PdfService.getInstance();
    private final RenderUltils renderUltils = RenderUltils.getInstance();

    private PdfProcessingService() {
    }

    public static PdfProcessingService getInstance() {
        if (instance == null) {
            synchronized (PdfProcessingService.class) {
                if (instance == null) {
                    instance = new PdfProcessingService();
                }
            }
        }
        return instance;
    }

    public PDDocument loadPdfFromFile(String filePath) {
        try {
            return Loader.loadPDF(new File(filePath));
        } catch (IOException e) {
            logger.error("Error loading PDF from file: {}", filePath, e);
            return null;
        }
    }

    public List<Chunk> splitPdfFileIntoChunks(String filePath) {
        PDDocument document = loadPdfFromFile(filePath);
        if (document == null) {
            logger.error("Failed to load PDF document from file: {}", filePath);
            return null;
        }
        return splitPdfFileIntoChunks(document);
    }

    public List<Chunk> splitPdfFileIntoChunks(PDDocument document) {
        if (document == null) {
            logger.error("Invalid PDF document provided.");
            return null;
        }

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String base64Content = renderUltils.encodeToBase64(byteArray);
            int totalLength = base64Content.length();

            List<Chunk> chunks = new ArrayList<>();
            int index = 0;

            for (int start = 0; start < totalLength; start += CHUNK_SIZE) {
                int end = Math.min(start + CHUNK_SIZE, totalLength);
                String chunkData = base64Content.substring(start, end);

                Chunk chunk = new Chunk();
                chunk.setIndex(index++);
                chunk.setValue(chunkData);

                chunks.add(chunk);
            }

            return chunks;
        } catch (IOException e) {
            logger.error("Error while reading or splitting PDF file", e);
            return null;
        }
    }

    public Optional<PDDocument> mergeChunksToPdfFile(int fileId) {
        List<Chunk> chunks = chunkService.findChunksByFileId(fileId);

        if (chunks == null || chunks.isEmpty()) {
            logger.error("No chunks found for file ID: {}", fileId);
            return Optional.empty();
        }

        // Sort chunks by index to ensure correct order
        List<Chunk> mutableChunks = new ArrayList<>(chunks);

        // Sort chunks by index to ensure correct order
        mutableChunks.sort(Comparator.comparingInt(Chunk::getIndex));

        try {
            StringBuilder base64Builder = new StringBuilder();
            for (Chunk chunk : mutableChunks) {
                if (chunk.getValue() != null) {
                    base64Builder.append(chunk.getValue());
                }
            }

            if (base64Builder.length() == 0) {
                logger.error("No valid chunk data found for file ID: {}", fileId);
                return Optional.empty();
            }

            byte[] pdfBytes = renderUltils.decodeFromBase64(base64Builder.toString());
            PDDocument document = Loader.loadPDF(pdfBytes);

            return Optional.of(document);
        } catch (IOException e) {
            logger.error("Error while merging chunks to PDF file for file ID: {}", fileId, e);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 data while merging chunks for file ID: {}", fileId, e);
            return Optional.empty();
        }
    }

    public Optional<PDDocument> mergeChunksToPdfFile(List<Chunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            logger.error("No chunks provided for merging.");
            return Optional.empty();
        }

        // Sort chunks by index to ensure correct order
        chunks.sort(Comparator.comparingInt(Chunk::getIndex));

        try {
            StringBuilder base64Builder = new StringBuilder();
            for (Chunk chunk : chunks) {
                if (chunk.getValue() != null) {
                    base64Builder.append(chunk.getValue());
                }
            }

            if (base64Builder.length() == 0) {
                logger.error("No valid chunk data found in provided chunks.");
                return Optional.empty();
            }

            byte[] pdfBytes = renderUltils.decodeFromBase64(base64Builder.toString());
            PDDocument document = Loader.loadPDF(pdfBytes);

            return Optional.of(document);
        } catch (IOException e) {
            logger.error("Error while merging chunks to PDF file", e);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 data while merging chunks", e);
            return Optional.empty();
        }
    }

    public int uploadPdfFile(User user, String filePath) {
        try {
            List<Chunk> chunks = splitPdfFileIntoChunks(filePath);
            PdfFile pdfFile = new PdfFile();
            pdfFile.setName(Paths.get(filePath).getFileName().toString());
            pdfFile.setUserId(user.getId());
            pdfFile.setTotalChunk((long) chunks.size());
            pdfFile.setTotalSize(new File(filePath).length());
            pdfFile.setLastModified(new Date());

            int fileId = pdfService.savePdf(pdfFile);
            logger.info("PDF file saved with ID: {}", fileId);
            for (Chunk chunk : chunks) {
                chunk.setFileId(fileId);
                chunkService.saveChunk(chunk);
            }
            return fileId;
        } catch (Exception e) {
            logger.error("Error uploading PDF file: {}", e.getMessage(), e);
            return -1;
        }
    }

    public List<String> loadPdfIntoImages(int fileId) {
        Optional<PDDocument> optionalDocument = mergeChunksToPdfFile(fileId);
        if (optionalDocument.isEmpty()) {
            logger.error("Failed to load PDF document for file ID: {}", fileId);
            return Collections.emptyList();
        }
        PDDocument document = optionalDocument.get();
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