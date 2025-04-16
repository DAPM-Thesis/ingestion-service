package com.dapm2.ingestion_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "anonymization_rules")
public class AnonymizationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_source_id", nullable = false, unique = true)
    private String dataSourceId;

    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules; // Stored as JSON string

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
