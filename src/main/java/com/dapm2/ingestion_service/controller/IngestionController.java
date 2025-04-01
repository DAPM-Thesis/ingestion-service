package com.dapm2.ingestion_service.controller;

import com.dapm2.ingestion_service.demo.MyEventAlgorithm;
import com.dapm2.ingestion_service.demo.MyEventOperator;
import com.dapm2.ingestion_service.demo.MySink;
import com.dapm2.ingestion_service.demo.MyStreamSource;
import communication.message.Message;
import communication.message.impl.event.Event;
import org.springframework.web.bind.annotation.*;
import pipeline.Pipeline;
import pipeline.PipelineBuilder;
import pipeline.processingelement.Sink;
import pipeline.processingelement.Source;
import pipeline.processingelement.operator.SimpleOperator;
import algorithm.Algorithm;

@RestController
@RequestMapping("/ingest")
public class IngestionController {

    private Pipeline pipeline;

    @PostMapping("/start")
    public String startIngestion() {
        if (pipeline != null) {
            return "⚠️ Ingestion pipeline is already running!";
        }
        MyStreamSource source = new MyStreamSource();
        source.start();
        return "✅ Ingestion pipeline started successfully!";
    }
}
