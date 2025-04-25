package com.dapm2.ingestion_service.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AnonymizationMappingMongoRepo
        extends MongoRepository<AnonymizationMappingDoc, String> {

    Optional<AnonymizationMappingDoc> findByDataSourceIdAndOriginalValue(
            String dataSourceId, String originalValue);

    Optional<AnonymizationMappingDoc> findByAnonymizedValue(String anonymizedValue);
}
