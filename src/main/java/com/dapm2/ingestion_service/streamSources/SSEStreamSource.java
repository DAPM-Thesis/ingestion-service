package com.dapm2.ingestion_service.streamSources;

import com.dapm2.ingestion_service.helperClasses.FlattenOtherAttributeToJson;
import com.dapm2.ingestion_service.helperClasses.JXESConverter;
import com.dapm2.ingestion_service.helperClasses.TimestampConverterISO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import communication.message.impl.Trace;
import communication.message.impl.event.Attribute;
import communication.message.impl.event.Event;
import pipeline.processingelement.Source;

import java.net.URI;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SSEStreamSource extends Source<Event> {

    private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
    private final EventSource eventSource;
    private final ObjectMapper mapper = new ObjectMapper(); // Jackson parser
    private final String sseUrl = "https://stream.wikimedia.org/v2/stream/recentchange";
    public SSEStreamSource() {
        EventHandler handler = new EventHandler() {
            public void onOpen() {}
            public void onClosed() {}
            public void onComment(String comment) {}

            public void onError(Throwable t) {
                System.err.println("SSE Error: " + t.getMessage());
            }

            public void onMessage(String event, MessageEvent messageEvent) throws Exception {
                String data = messageEvent.getData();
                JsonNode json = mapper.readTree(data);

                // Extract main event fields
                String caseId = json.path("meta").path("id").asText("unknown_case");
                String activity = json.path("type").asText("unknown_type");
                Object rawTimestamp = json.path("timestamp").isNumber()
                        ? json.path("timestamp").longValue()
                        : json.path("timestamp").asText();
                String timestamp = TimestampConverterISO.toISO(rawTimestamp);

                // Collect all other fields as flat key=value attributes
                Set<Attribute<?>> eventAttributes = new HashSet<>();
                FlattenOtherAttributeToJson.flatten(json, "", eventAttributes);

                // Build DAPM Event
                Event dapmEvent = new Event(caseId, activity, timestamp, eventAttributes);

                // Convert to JXES string
                Trace trace = new Trace(List.of(dapmEvent));
                String jxesJson = JXESConverter.convertTraceToJXES(trace);

                // Add JXES string as an attribute
                eventAttributes.add(new Attribute<>("jxespayload", "string:" + jxesJson));

                // Rebuild event with updated attributes
                Event eventWithJXES = new Event(caseId, activity, timestamp, eventAttributes);
            }
        };

        this.eventSource = new EventSource.Builder(handler, URI.create(sseUrl)).build();
    }

    @Override
    public Event process() {
        try {
            return eventQueue.take(); // blocks until event is available
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public void start() {
        eventSource.start();
        super.start();
    }

    public void stop() {
        eventSource.close();
    }
}
