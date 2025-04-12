package com.dapm2.ingestion_service.dto;

import java.util.Map;

public class FilterConfigDTO {
    private Map<String, Object> filters;

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
}
