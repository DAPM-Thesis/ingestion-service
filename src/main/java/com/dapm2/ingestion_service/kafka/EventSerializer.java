// src/main/java/com/dapm2/ingestion_service/kafka/EventSerializer.java
package com.dapm2.ingestion_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import communication.message.impl.event.Attribute;
import communication.message.impl.event.Event;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class EventSerializer implements Serializer<Event> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(@SuppressWarnings("rawtypes") Map<String, ?> configs, boolean isKey) {
        // no-op
    }

    @Override
    public byte[] serialize(String topic, Event data) {
        if (data == null) {
            return null;
        }
        try {
            // root JSON object
            ObjectNode root = mapper.createObjectNode();
            root.put("caseID",   data.getCaseID());
            root.put("activity", data.getActivity());
            root.put("timestamp", data.getTimestamp());

            // attributes as a nested object
            ObjectNode attrNode = mapper.createObjectNode();
            for (Attribute<?> attr : data.getAttributes()) {
                String name = attr.getName();
                Object  value = attr.getValue();

                if (value == null) {
                    attrNode.putNull(name);
                } else if (value instanceof Integer) {
                    attrNode.put(name, (Integer) value);
                } else if (value instanceof Long) {
                    attrNode.put(name, (Long) value);
                } else if (value instanceof Double) {
                    attrNode.put(name, (Double) value);
                } else {
                    // fallback to string
                    attrNode.put(name, value.toString());
                }
            }
            root.set("attributes", attrNode);

            return mapper.writeValueAsBytes(root);
        } catch (JsonProcessingException e) {
            // include the cause so you can see exactly what failed
            throw new SerializationException("Error serializing Event to JSON", e);
        }
    }

    @Override
    public void close() {
        // no-op
    }
}
