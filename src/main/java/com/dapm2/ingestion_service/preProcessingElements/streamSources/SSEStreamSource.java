// src/main/java/com/dapm2/ingestion_service/preProcessingElements/streamSources/SSEStreamSource.java
package com.dapm2.ingestion_service.preProcessingElements.streamSources;

import com.dapm2.ingestion_service.config.SpringContext;
import com.dapm2.ingestion_service.mongo.AnonymizationMappingService;
import com.dapm2.ingestion_service.preProcessingElements.AnonymizationProcess;
import com.dapm2.ingestion_service.preProcessingElements.AttributeSettingProcess;
import com.dapm2.ingestion_service.preProcessingElements.FiltrationProcess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import communication.message.impl.event.Event;
import pipeline.processingelement.source.SimpleSource;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SSEStreamSource extends SimpleSource<Event> {

    private static final String SSE_URL      = "https://stream.wikimedia.org/v2/stream/recentchange";
    private static final long   FILTERING_ID = 1L;
    private static final long   ATTRIBUTE_ID = 2L;
    private static final String SOURCE_ID    = "wiki";

    private final BlockingQueue<Event>         eventQueue;
    private final EventSource                  eventSource;
    private final ObjectMapper                 mapper;
    private final FiltrationProcess            filtrationProcess;
    private final AttributeSettingProcess      attributeProcess;
    private final AnonymizationProcess         anonymizationProcess;
    private final AnonymizationMappingService  anonymizationMappingService;

    public SSEStreamSource() {
        this.eventQueue             = new LinkedBlockingQueue<>();
        this.mapper                 = new ObjectMapper();

        // load once at startup (deferred until constructor now)
        this.filtrationProcess      = FiltrationProcess.fromFilterId(FILTERING_ID);
        this.attributeProcess       = AttributeSettingProcess.fromSettingId(ATTRIBUTE_ID);
        this.anonymizationProcess   = AnonymizationProcess.fromDataSourceId(SOURCE_ID);
        this.anonymizationMappingService = SpringContext.getBean(AnonymizationMappingService.class);

        EventHandler handler = new EventHandler() {
            @Override public void onOpen() {}
            @Override public void onClosed() {}
            @Override public void onComment(String comment) {}

            @Override
            public void onError(Throwable t) {
                System.err.println("SSE Error: " + t.getMessage());
            }

            @Override
            public void onMessage(String event, MessageEvent messageEvent) throws Exception {
                JsonNode json = mapper.readTree(messageEvent.getData());
                // Optional: Store raw JSON in Mongo
                anonymizationMappingService.saveRawData(SOURCE_ID, json);

                // 1) filter
                if (!filtrationProcess.shouldPass(json)) {
                    return;
                }
                // 2) anonymize
                json = anonymizationProcess.apply(json);
                // 3) attribute setting
                Event dapmEvent = attributeProcess.extractEvent(json);

                // enqueue for downstream
                eventQueue.put(dapmEvent);
                System.out.println("Ingested Value:" + dapmEvent);
            }
        };

        this.eventSource = new EventSource.Builder(handler, URI.create(SSE_URL)).build();
    }

    @Override
    public Event process() {
        try {
            return eventQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public void start() {
        eventSource.start();
    }

    public void stop() {
        eventSource.close();
    }
}
