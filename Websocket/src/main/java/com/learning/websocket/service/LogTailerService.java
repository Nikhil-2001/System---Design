package com.learning.websocket.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LogTailerService {
    private final LogWebSocketHandler webSocketHandler;
    private final File file;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "log-tailer");
        t.setDaemon(true);
        return t;
    });


    private volatile long filePointer = 0L;
    private RandomAccessFile reader;


    public LogTailerService(LogWebSocketHandler webSocketHandler,
                            @Value("${tail.file.path:./application.log}") String filePath) {
        this.webSocketHandler = webSocketHandler;
        this.file = new File(filePath);
    }


    @PostConstruct
    public void start() {
        try {
            if (!file.exists()) file.createNewFile();
            reader = new RandomAccessFile(file, "r");
            filePointer = reader.length();
            executor.scheduleWithFixedDelay(this::poll, 0, 500, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start tailer", e);
        }
    }


    private void poll() {
        try {
            long len = file.length();
            if (len < filePointer) {
                filePointer = 0L;
                reader.seek(0);
            }


            if (len > filePointer) {
                reader.seek(filePointer);
                String line;
                while ((line = reader.readLine()) != null) {
                    byte[] bytes = line.getBytes("ISO-8859-1");
                    String utf8Line = new String(bytes, StandardCharsets.UTF_8);
                    webSocketHandler.broadcastLine(utf8Line);
                }
                filePointer = reader.getFilePointer();
            }
        } catch (IOException e) {
        }
    }


    @PreDestroy
    public void stop() {
        executor.shutdownNow();
        try {
            if (reader != null) reader.close();
        } catch (IOException ignored) {}
    }
}