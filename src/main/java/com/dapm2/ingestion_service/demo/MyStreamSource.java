// src/main/java/com/dapm2/ingestion_service/demo/MyStreamSource.java
package com.dapm2.ingestion_service.demo;

import com.dapm2.ingestion_service.config.SpringContext;
import com.dapm2.ingestion_service.mongo.AnonymizationMappingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

import java.net.URI;

public class MyStreamSource {

    private final EventSource eventSource;
    private final AnonymizationMappingService mappingService;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * @param url      the SSE endpoint to connect to, e.g. "https://stream.wikimedia.org/v2/stream/recentchange"
     * @param sourceId a logical name for this feed, e.g. "MediaWikiRevisionCreate"
     */
    public MyStreamSource(String url, String sourceId) {
        // grab the Mongo service from Spring
        this.mappingService = SpringContext.getBean(AnonymizationMappingService.class);

        // build a handler that writes each JSON payload into Mongo
        EventHandler handler = new EventHandler() {
            @Override public void onOpen()                 { /* no-op */ }
            @Override public void onClosed()               { /* no-op */ }
            @Override public void onComment(String comment){ /* no-op */ }

            @Override
            public void onError(Throwable t) {
                System.err.println("SSE Error: " + t.getMessage());
            }

            @Override
            public void onMessage(String event, MessageEvent me) throws Exception {
                JsonNode json = mapper.readTree(me.getData());
                // Saves under collection named by sourceId
                mappingService.saveRawData(sourceId, json);
                System.out.println("Ingested from [" + sourceId + "]: " + json.toString());
            }
        };

        // create the EventSource client
        this.eventSource = new EventSource.Builder(handler, URI.create(url)).build();
    }

    /** Start the SSE client (non–blocking). */
    public void start() {
        eventSource.start();
    }

    /** Stop the SSE client and clean up. */
    public void stop() {
        eventSource.close();
    }
}
