package bg.sofia.uni.fmi.mjt.splitwise.streams;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ReaderWriterCreator {
    private String directory;

    public ReaderWriterCreator(String directory) {
        this.directory = directory;
    }

    public Reader getRead() {
        try {
            return new FileReader(directory);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not use the directory for reading", e);
        }
    }

    public Writer getAppend() {
        try {
            return new FileWriter(directory, true);
        } catch (IOException e) {
            throw new RuntimeException("Could not use the directory for writing", e);
        }
    }

    public Writer getNotAppend() {
        try {
            return new FileWriter(directory, false);
        } catch (IOException e) {
            throw new RuntimeException("Could not use the directory for writing", e);
        }
    }
}

