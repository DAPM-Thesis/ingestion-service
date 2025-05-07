// src/main/java/com/dapm2/ingestion_service/service/IngestionService.java
package com.dapm2.ingestion_service.service;

import com.dapm2.ingestion_service.demo.MyStreamSource;
import com.dapm2.ingestion_service.preProcessingElements.streamSources.SSEStreamSource;
import communication.message.impl.event.Event;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class IngestionService {
    private final Sinks.Many<Event> sink =
            Sinks.many().multicast().onBackpressureBuffer();
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();
    private volatile boolean running = false;

    public IngestionService() {
        // KafkaProducerService has been removed
    }

    public synchronized Flux<Event> startIngestionFlux() {
        if (!running) {
            running = true;
            executor.submit(this::runStreamSource);
        }
        return sink.asFlux();
    }

    private void runStreamSource() {
        SSEStreamSource src = new SSEStreamSource();
        src.start();
        Event e;
        while ((e = src.process()) != null) {
            sink.tryEmitNext(e);
        }
    }

    public synchronized void onlyIngestion(String url, String sourceId) {
        MyStreamSource src = new MyStreamSource(url, sourceId);
        src.start();
        // keep a reference if you need to call src.stop() later
    }
}
