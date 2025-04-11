package com.dapm2.ingestion_service.preProcessingElements.streamSources;

import com.dapm2.ingestion_service.entity.AttributeSetting;
import com.dapm2.ingestion_service.kafka.KafkaProducerService;
import com.dapm2.ingestion_service.preProcessingElements.AttributeSettings;
import com.dapm2.ingestion_service.utils.FlattenOtherAttributeToJson;
import com.dapm2.ingestion_service.utils.JXESUtil;
import com.dapm2.ingestion_service.utils.TimestampConverterISO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
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
    private final String ingestionTopic= "ingested_data";
    private final long idR= (long) 3;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper mapper = new ObjectMapper(); // Jackson parser
    private final String sseUrl = "https://stream.wikimedia.org/v2/stream/recentchange";
    public SSEStreamSource(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
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

                AttributeSettings attributeSettings = AttributeSettings.fromSettingId(idR);

                Event dapmEvent = attributeSettings.extractEvent(json);

                System.out.println("Ingested Event: " + dapmEvent);
                String jxes = JXESUtil.toJXES(dapmEvent);

                kafkaProducerService.sendJXES(ingestionTopic, jxes);
                eventQueue.put(dapmEvent);
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
        //super.start();
    }

    public void stop() {
        eventSource.close();
    }
}
