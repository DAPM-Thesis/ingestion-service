package com.dapm2.ingestion_service.controller;

import com.dapm2.ingestion_service.demo.MyEventAlgorithm;
import com.dapm2.ingestion_service.demo.MyEventOperator;
import com.dapm2.ingestion_service.demo.MySink;
import com.dapm2.ingestion_service.demo.MyStreamSource;
import com.dapm2.ingestion_service.service.IngestionService;
import communication.message.Message;
import communication.message.impl.event.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pipeline.Pipeline;
import pipeline.PipelineBuilder;
import pipeline.processingelement.Sink;
import pipeline.processingelement.Source;
import pipeline.processingelement.operator.SimpleOperator;
import algorithm.Algorithm;

@RestController
@RequestMapping("/ingest")
@Tag(name = "Ingestion API", description = "Manage Ingestion Tasks")
public class IngestionController {

    private final IngestionService ingestionService;
    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }
    @Operation(summary = "Start Ingestion", description = "Trigger Ingestion from source.")
    @PostMapping("/start")
    public String startIngestion() {
        return ingestionService.startIngestion();
    }
    @Operation(summary = "Stop Ingestion", description = "Stop Ingestion from source.")
    @PostMapping("/stop")
    public String stopIngestion() {
        return null;
    }
}
