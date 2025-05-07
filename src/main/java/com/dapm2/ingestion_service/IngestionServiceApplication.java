package com.dapm2.ingestion_service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
@EntityScan(basePackages = "com.dapm2.ingestion_service.entity")
@EnableJpaRepositories(basePackages = "com.dapm2.ingestion_service.repository")
@EnableMongoRepositories(basePackages = "com.dapm2.ingestion_service.mongo")
@SpringBootApplication
@ComponentScan(basePackages = {
		"com.dapm2.ingestion_service",      // your controllers/services/entities
		"pipeline.processingelement",       // pipeline API beans (Source, Sink, etc.)
})
public class IngestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestionServiceApplication.class, args);
	}

}
