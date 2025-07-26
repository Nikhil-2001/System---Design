package com.learning.SpringBoot.Ad_Hoc;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

@RestController
public class PdfStreamController {

    // Use to stream large files it happens chunk by chunk
    @GetMapping("/stream/pdf")
    public ResponseEntity<StreamingResponseBody> streamPdf() {

        FileSystemResource fileResource = new FileSystemResource("GradleNotes.pdf");

        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = fileResource.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush(); // Important: flush to client in real-time
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=large.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }

    // Use to stream data message by message
    @GetMapping("/events")
    public ResponseBodyEmitter streamEvents() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    emitter.send("event-" + i + "\n", MediaType.TEXT_PLAIN);
                    Thread.sleep(100);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    // This is best used for data streaming, it send one by one
    @GetMapping("/sse")
    public SseEmitter sseEmitter() {
        SseEmitter emitter = new SseEmitter();
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    emitter.send(SseEmitter.event().data("event-" + i));
                    Thread.sleep(100);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }


}
