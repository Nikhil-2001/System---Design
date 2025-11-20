package com.learning.websocket.learning;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHandling {

    private static final String FILE_PATH = "some.log";
    private static final long POLL_INTERVAL_MS = 500;
    private static final String MESSAGE = "Nikhil";
    private static final long WRITE_INTERVAL_MS = 100;
    private static final long TOTAL_RUNTIME_MS = 5000;

    public static void main(String[] args) throws InterruptedException {
        Thread writerThread = new Thread(FileHandling::continuouslyAppendToFile, "WriterThread");
        Thread readerThread = new Thread(FileHandling::readFile, "ReaderThread");

        writerThread.start();
        readerThread.start();

        Thread.sleep(TOTAL_RUNTIME_MS);

        writerThread.interrupt();
        readerThread.interrupt();

        writerThread.join();
        readerThread.join();

        System.out.println("\n Both threads stopped cleanly after 5 seconds.");
    }

    private static void continuouslyAppendToFile() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            int i = 0;
            while (!Thread.currentThread().isInterrupted()) {
                writer.write(MESSAGE + " - " + i + System.lineSeparator());
                writer.flush();
                i++;
                Thread.sleep(WRITE_INTERVAL_MS);
            }
        } catch (IOException e) {
            System.err.println("[Writer] IO Error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("[Writer] Exiting thread.");
        }
    }

    private static void readFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.err.println("[Reader] File not found: " + FILE_PATH);
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long filePointer = raf.getFilePointer();
            while (!Thread.currentThread().isInterrupted()) {
                long fileLength = file.length();
                if (fileLength < filePointer) {
                    System.out.println("[Reader] File truncated, resetting pointer...");
                    filePointer = 0;
                    raf.seek(0);
                }
                if (fileLength > filePointer) {
                    raf.seek(filePointer);
                    String line;
                    while ((line = raf.readLine()) != null) {
                        System.out.println("[Reader] " + line);
                    }
                    filePointer = raf.getFilePointer();
                }
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[Reader] IO Error: " + e.getMessage());
        } finally {
            System.out.println("[Reader] Exiting thread.");
        }
    }
}