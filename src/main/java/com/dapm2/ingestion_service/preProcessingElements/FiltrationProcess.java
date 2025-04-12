package com.dapm2.ingestion_service.preProcessingElements;

import com.dapm2.ingestion_service.config.SpringContext;
import com.dapm2.ingestion_service.entity.FilterConfig;
import com.dapm2.ingestion_service.service.StreamConfigurationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class FiltrationProcess {

    private final Map<String, Object> filters;

    private FiltrationProcess(Map<String, Object> filters) {
        this.filters = filters;
    }

    public static FiltrationProcess fromFilterId(Long id) {
        StreamConfigurationService service = SpringContext.getBean(StreamConfigurationService.class);
        ObjectMapper mapper = SpringContext.getBean(ObjectMapper.class);

        FilterConfig config = service.getFilterById(id);
        if (config == null || config.getFilters() == null) {
            throw new RuntimeException("No FilterConfig found with id: " + id);
        }

        try {
            Map<String, Object> filters = mapper.readValue(config.getFilters(), Map.class);
            return new FiltrationProcess(filters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse filters", e);
        }
    }

    public boolean shouldPass(JsonNode eventJson) {
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object expectedValue = entry.getValue();

            JsonNode actualNode = eventJson.path(key);
            if (actualNode.isMissingNode()) return false;

            if (expectedValue instanceof Boolean && actualNode.isBoolean()) {
                if (actualNode.booleanValue() != (Boolean) expectedValue) return false;
            } else if (expectedValue instanceof String && actualNode.isTextual()) {
                if (!actualNode.textValue().equals(expectedValue)) return false;
            } else {
                // fallback comparison
                if (!actualNode.toString().equals(expectedValue.toString())) return false;
            }
        }
        return true;
    }
}
