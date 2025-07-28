package com.dev.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.dev.model.Chunk;
import com.dev.util.RenderUltils;

public class ExecuteFileService {

    private static final Logger logger = LogManager.getLogger(ExecuteFileService.class);
    private static final int CHUNK_SIZE = 2 * 1024 * 1024; // 2 MB

    private static ExecuteFileService instance;

    private final ChunkService chunkService = ChunkService.getInstance();
    private final RenderUltils renderUltils = RenderUltils.getInstance();

    private ExecuteFileService() {
    }

    public static ExecuteFileService getInstance() {
        if (instance == null) {
            synchronized (ExecuteFileService.class) {
                if (instance == null) {
                    instance = new ExecuteFileService();
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
}