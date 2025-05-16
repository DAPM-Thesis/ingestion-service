package com.dapm2.ingestion_service.controller;

import com.dapm2.ingestion_service.dto.MiningResultDTO;
import com.dapm2.ingestion_service.entity.MiningResult;
import com.dapm2.ingestion_service.service.MiningResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sink/mining-result")
public class MiningResultController {

    private final MiningResultService service;

    public MiningResultController(MiningResultService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<MiningResult> saveResult(@RequestBody MiningResultDTO dto) {

        System.out.println(" Controller reached!");
        return ResponseEntity.ok(service.saveResult(dto));

    }
}
