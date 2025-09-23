package com.searchengine.persistence;

import com.searchengine.core.InvertedIndex;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class PersistenceService {
    private static final Logger logger = Logger.getLogger(PersistenceService.class.getName());
    private static final String DATA_DIR = "search-engine-data";
    private static final String INDEX_FILE_SUFFIX = "_index.ser";

    public PersistenceService() {
        createDataDirectory();
    }

    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public void saveShardMap(Map<String, InvertedIndex> shardMap) {
        for (Map.Entry<String, InvertedIndex> entry : shardMap.entrySet()) {
            saveIndex(entry.getKey(), entry.getValue());
        }
        logger.info("Saved " + shardMap.size() + " shards to disk");
    }

    public Map<String, InvertedIndex> loadShardMap() {
        Map<String, InvertedIndex> shardMap = new ConcurrentHashMap<>();
        File dataDir = new File(DATA_DIR);

        if (!dataDir.exists()) {
            logger.info("No existing data directory found, starting fresh");
            return shardMap;
        }

        File[] indexFiles = dataDir.listFiles((dir, name) -> name.endsWith(INDEX_FILE_SUFFIX));
        if (indexFiles != null) {
            for (File file : indexFiles) {
                String nodeId = file.getName().replace(INDEX_FILE_SUFFIX, "");
                InvertedIndex index = loadIndex(nodeId);
                if (index != null) {
                    shardMap.put(nodeId, index);
                }
            }
        }

        logger.info("Loaded " + shardMap.size() + " shards from disk");
        return shardMap;
    }

    private void saveIndex(String nodeId, InvertedIndex index) {
        try {
            File file = new File(DATA_DIR, nodeId + INDEX_FILE_SUFFIX);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(index);
            }
            logger.info("Saved index for node: " + nodeId);
        } catch (IOException e) {
            logger.severe("Failed to save index for node " + nodeId + ": " + e.getMessage());
        }
    }

    private InvertedIndex loadIndex(String nodeId) {
        try {
            File file = new File(DATA_DIR, nodeId + INDEX_FILE_SUFFIX);
            if (!file.exists()) {
                return null;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                InvertedIndex index = (InvertedIndex) ois.readObject();
                logger.info("Loaded index for node: " + nodeId);
                return index;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Failed to load index for node " + nodeId + ": " + e.getMessage());
            return null;
        }
    }

    public void deletePersistedData() {
        File dataDir = new File(DATA_DIR);
        if (dataDir.exists()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dataDir.delete();
            logger.info("Deleted all persisted data");
        }
    }
}