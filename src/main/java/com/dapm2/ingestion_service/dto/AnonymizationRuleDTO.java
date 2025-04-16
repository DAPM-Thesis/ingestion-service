package com.dapm2.ingestion_service.dto;

import java.util.Map;

public class AnonymizationRuleDTO {
    private String dataSourceId;
    private Map<String, String> rules;

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public void setRules(Map<String, String> rules) {
        this.rules = rules;
    }
}
