package com.dapm2.ingestion_service.service;

import com.dapm2.ingestion_service.demo.MyStreamSource;
import com.dapm2.ingestion_service.kafka.KafkaProducerService;
import com.dapm2.ingestion_service.preProcessingElements.streamSources.SSEStreamSource;
import communication.Producer;
import communication.message.impl.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pipeline.processingelement.Source;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class IngestionService {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean isRunning = false;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    public IngestionService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }
    public String startIngestion() {
        if (isRunning) {
            return "Ingestion already running.";
        }

        isRunning = true;

        executor.submit(() -> {
            try {
                SSEStreamSource source = new SSEStreamSource(kafkaProducerService);
                source.start();  // this will use the event source to begin ingestion
            } catch (Exception e) {
                e.printStackTrace();
                isRunning = false;
            }
        });

        return "Ingestion started successfully.";
    }
}
