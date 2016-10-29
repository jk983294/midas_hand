package com.victor.midas.services.worker.task.lucene;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class SingleSearcher {

    private static volatile IndexSearcher uniqueInstance;
    private SingleSearcher() {
    }

    // double null check
    public static IndexSearcher getInstance(String indexPath) throws IOException {
        if (uniqueInstance == null) {
            synchronized (SingleSearcher.class) {
                if (uniqueInstance == null) {
                    Directory indexDirectory = FSDirectory.open(Paths.get(indexPath), NoLockFactory.INSTANCE);
                    DirectoryReader reader = DirectoryReader.open(indexDirectory);
                    uniqueInstance = new IndexSearcher(reader);
                }
            }
        }
        return uniqueInstance;
    }
}
