package com.dapm2.ingestion_service.helperClasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import communication.message.impl.Trace;
import communication.message.impl.event.Attribute;
import communication.message.impl.event.Event;

public class JXESConverter {

    public static String convertTraceToJXES(Trace trace) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode tracesArray = mapper.createArrayNode();

        ObjectNode traceNode = mapper.createObjectNode();
        traceNode.put("traceID", trace.getCaseID());

        ArrayNode eventsArray = mapper.createArrayNode();
        for (Event e : trace) {
            ObjectNode eventNode = mapper.createObjectNode();
            eventNode.put("activity", e.getActivity());
            eventNode.put("timestamp", e.getTimestamp());

            ObjectNode attrNode = mapper.createObjectNode();
            for (Attribute<?> attr : e.getAttributes()) {
                Object value = attr.getValue();
                String valueWithType;

                if (value instanceof String) {
                    String safeString = value.toString()
                            .replace("\"", "'")        // Replace inner quotes to avoid breaking JSON
                            .replace("=", ":")         // Optional: sanitize equals signs
                            .replace("\n", " ")        // Remove newlines
                            .trim();

                    valueWithType = "string:" + safeString;
                } else if (value instanceof Integer) {
                    valueWithType = "int:" + value;
                } else if (value instanceof Boolean) {
                    valueWithType = "boolean:" + value;
                } else {
                    valueWithType = "string:" + value.toString().replace("\"", "'").trim();
                }

                attrNode.put(attr.getName(), valueWithType);
            }

            eventNode.set("attributes", attrNode);

            eventsArray.add(eventNode);
        }

        traceNode.set("events", eventsArray);
        tracesArray.add(traceNode);
        root.set("traces", tracesArray);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "{}";
        }
    }
}
