package com.dapm2.ingestion_service.service;

import com.dapm2.ingestion_service.demo.MyStreamSource;
import communication.message.impl.event.Event;
import org.springframework.stereotype.Service;
import pipeline.processingelement.Source;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class IngestionService {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean isRunning = false;

    public String startIngestion() {
        if (isRunning) {
            return "Ingestion already running.";
        }

        isRunning = true;

        executor.submit(() -> {
            try {
                Source<Event> source = new MyStreamSource();
                source.start();
            } catch (Exception e) {
                e.printStackTrace();
                isRunning = false;
            }
        });

        return "Ingestion started successfully.";
    }
}
