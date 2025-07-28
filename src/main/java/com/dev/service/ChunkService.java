package com.dev.service;

import java.util.List;
import java.util.Optional;

import com.dev.dao.ChunkDao;
import com.dev.model.Chunk;

public class ChunkService {
    private static ChunkDao chunkDao = ChunkDao.getInstance();
    private static ChunkService instance;

    private ChunkService() {
    }

    public static ChunkService getInstance() {
        if (instance == null) {
            synchronized (ChunkService.class) {
                if (instance == null) {
                    instance = new ChunkService();
                }
            }
        }
        return instance;
    }

    public int saveChunk(Chunk chunk) {
        return chunkDao.save(chunk);
    }

    public void deleteChunk(int id) {
        chunkDao.delete(id);
    }

    public Optional<Chunk> findChunkById(int id) {
        return Optional.ofNullable(chunkDao.findById(id));
    }

    public void updateChunk(Chunk chunk) {
        chunkDao.update(chunk);
    }

    public Optional<List<Chunk>> findAllChunks() {
        List<Chunk> chunks = chunkDao.findAll();
        return chunks.isEmpty() ? Optional.empty() : Optional.of(chunks);
    }

    public List<Chunk> findChunksByFileId(int fileId) {
        List<Chunk> chunks = chunkDao.findAll();
        chunks = chunks.stream().filter(chunk -> chunk.getFileId() == fileId)
                .sorted((c1, c2) -> Integer.compare(c1.getIndex(), c2.getIndex())).toList();
        return chunks;
    }
}
