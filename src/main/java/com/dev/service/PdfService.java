package com.dev.service;

import java.util.List;
import java.util.Optional;

import com.dev.dao.PdfDao;
import com.dev.model.PdfFile;

public class PdfService {
    private static PdfDao pdfDao = PdfDao.getInstance();

    private static PdfService instance;

    private PdfService() {
    }

    public static PdfService getInstance() {
        if (instance == null) {
            synchronized (PdfService.class) {
                if (instance == null) {
                    instance = new PdfService();
                }
            }
        }
        return instance;
    }

    public int savePdf(PdfFile pdf) {
        return pdfDao.save(pdf);
    }

    public void deletePdf(int id) {
        pdfDao.delete(id);
    }

    public Optional<PdfFile> findPdfById(int id) {
        return Optional.ofNullable(pdfDao.findById(id));
    }

    public void updatePdf(PdfFile pdf) {
        pdfDao.update(pdf);
    }

    public Optional<List<PdfFile>> findAllPdfs() {
        return Optional.of(pdfDao.findAll());
    }
}
