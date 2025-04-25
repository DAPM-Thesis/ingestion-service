package com.dapm2.ingestion_service.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AnonymizationMappingMongoService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AnonymizationMappingMongoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /** gives a per-source collection name */
    private String collectionFor(String dataSourceId) {
        return "mappingTableFor_" + dataSourceId;
    }

    public String anonymize(String dataSourceId, String fieldName, String originalValue) {
        String coll = collectionFor(dataSourceId);

        Query q = Query.query(
                Criteria.where("fieldName").is(fieldName)
                        .and("originalValue").is(originalValue)
        );
        AnonymizationMappingDoc existing =
                mongoTemplate.findOne(q, AnonymizationMappingDoc.class, coll);

        if (existing != null) {
            System.out.println("Duplicate value found!!!!!");
            return existing.getAnonymizedValue();
        }
        else {
            String safeField = fieldName.trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");   // spaces → underscores, strip weird chars
            String token;
            do {
                int rnd = ThreadLocalRandom.current()
                        .nextInt(1, 1_000_000);  // [100000..999999]
                token = safeField + "_" + rnd;
                // repeat if this token already exists in the collection
            } while (mongoTemplate.exists(
                    Query.query(Criteria.where("anonymizedValue").is(token)),
                    AnonymizationMappingDoc.class,
                    coll
            ));
            AnonymizationMappingDoc doc = AnonymizationMappingDoc.builder()
                    .dataSourceId(dataSourceId)
                    .fieldName(fieldName)
                    .originalValue(originalValue)
                    .anonymizedValue(token)
                    .createdAt(Instant.now())
                    .build();

            mongoTemplate.save(doc, coll);
            return token;
        }
    }

    public Optional<String> deanonymize(String dataSourceId, String fieldName, String token) {
        String coll = collectionFor(dataSourceId);

        Query q = Query.query(
                Criteria.where("fieldName").is(fieldName)
                        .and("anonymizedValue").is(token)
        );
        AnonymizationMappingDoc doc =
                mongoTemplate.findOne(q, AnonymizationMappingDoc.class, coll);

        return Optional.ofNullable(doc)
                .map(AnonymizationMappingDoc::getOriginalValue);
    }
}
