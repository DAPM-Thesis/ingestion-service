// src/main/java/com/dapm2/ingestion_service/mongo/AnonymizationMappingDoc.java
package com.dapm2.ingestion_service.mongo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class AnonymizationMappingDoc {
    @Id
    private String id;
    private String dataSourceId;
    private String fieldName;
    private String originalValue;
    private String anonymizedValue;
    private Instant createdAt;
}