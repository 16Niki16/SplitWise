package bg.sofia.uni.fmi.mjt.splitwise.helpers;

import bg.sofia.uni.fmi.mjt.splitwise.streams.ReaderWriterCreator;

import java.io.BufferedWriter;
import java.io.IOException;

public class ExceptionFormater {
    public static void exceptionAdd(String username, String message, StackTraceElement[] stackTrace,
                                    ReaderWriterCreator directory) {
        try (BufferedWriter wr = new BufferedWriter(directory.getAppend())) {
            wr.write(username + " | " + message + " | stackTrace:");
            wr.newLine();
            for (StackTraceElement trace : stackTrace) {
                wr.write(trace.toString());
                wr.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
