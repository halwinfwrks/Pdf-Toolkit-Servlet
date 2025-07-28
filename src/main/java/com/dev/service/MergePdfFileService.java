package com.dev.service;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.dev.model.Chunk;
import com.dev.model.PdfFile;
import com.dev.model.User;

public class MergePdfFileService {

    private static final Logger logger = LogManager.getLogger(MergePdfFileService.class);
    private static MergePdfFileService instance;
    private static PdfProcessingService pdfProcessingService = PdfProcessingService.getInstance();

    private MergePdfFileService() {
    }

    public static MergePdfFileService getInstance() {
        if (instance == null) {
            synchronized (MergePdfFileService.class) {
                if (instance == null) {
                    instance = new MergePdfFileService();
                }
            }
        }
        return instance;
    }

    public int mergePdfs(User user, int file1, int file2) {
        logger.info("Merging PDF files have IDs: {} and {}", file1, file2);
        // Logic to merge PDF files

        // Step 1: get PDDocument instances for both files
        PDDocument doc1 = pdfProcessingService.mergeChunksToPdfFile(file1).get();
        PDDocument doc2 = pdfProcessingService.mergeChunksToPdfFile(file2).get();

        if (doc1 == null || doc2 == null) {
            logger.error("Failed to load one or both PDF documents for merging.");
            return -1;
        }

        // Step 2: Merge the documents
        try {
            PDDocument mergedDocument = new PDDocument();
            for (PDPage page : doc1.getPages())
                mergedDocument.addPage(page);

            for (PDPage page : doc2.getPages())
                mergedDocument.addPage(page);

            // Save the merged document to a file and update to database
            String outputFilePath = "merged_output.pdf";
            mergedDocument.save(outputFilePath);

            int fileId = pdfProcessingService.uploadPdfFile(user, outputFilePath);
            if (fileId == -1) {
                logger.error("Failed to upload merged PDF file.");
                return -1;
            }
            logger.info("PDF files merged successfully into {}", outputFilePath);

            // remove temporary files
            doc1.close();
            doc2.close();
            mergedDocument.close();

            return fileId;
        } catch (Exception e) {
            logger.error("Error merging PDF files: {}", e.getMessage(), e);
            return -1;
        }
    }

}
