package com.dapm2.ingestion_service;

import algorithm.Algorithm;
import com.dapm2.ingestion_service.demo.MyEventAlgorithm;
import com.dapm2.ingestion_service.demo.MyEventOperator;
import com.dapm2.ingestion_service.demo.MySink;
import com.dapm2.ingestion_service.preProcessingElements.streamSources.SSEStreamSource;
import communication.message.Message;
import communication.message.impl.event.Event;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pipeline.Pipeline;
import pipeline.PipelineBuilder;
import pipeline.processingelement.Sink;
import pipeline.processingelement.Source;
import pipeline.processingelement.operator.SimpleOperator;

@SpringBootApplication(scanBasePackages = "com.dapm2.ingestion_service")
public class IngestionServiceApplication {

	public static void main(String[] args) {
//		// Pipeline: Source<Event> -> Operator<Event, Event, String, String> -> Sink<Event>
//
//		// Source
//		Source<Event> source = new SSEStreamSource();
//		//Source<Event> source = new MyStreamSource();
//
//		// Event Operator
//		Algorithm<Message, Event> algorithm = new MyEventAlgorithm();
//		SimpleOperator<Event> operator = new MyEventOperator(algorithm);
//
//		// Sink
//		Sink sink = new MySink();
//
//		// Create pipeline using pipeline builder
//		PipelineBuilder builder = new PipelineBuilder();
//		Pipeline pipeline = builder.createPipeline()
//				.addProcessingElement(source)
//				.addProcessingElement(operator)
//				.addProcessingElement(sink)
//				.connect(source, operator)
//				.connect(operator, sink)
//				.getCurrentPipeline();
//
//		pipeline.start();
		SpringApplication.run(IngestionServiceApplication.class, args);
	}

}
