package com.dapm2.ingestion_service.controller;

import com.dapm2.ingestion_service.dto.AnonymizationRuleDTO;
import com.dapm2.ingestion_service.entity.AnonymizationRule;
import com.dapm2.ingestion_service.service.AnonymizationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/anonymization")
public class AnonymizationRuleController {

    @Autowired
    private AnonymizationRuleService service;

    //  Save
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody AnonymizationRuleDTO request) {
        service.saveRule(request);
        return ResponseEntity.ok("Anonymization rule saved successfully!");
    }

    //  Get All
    @GetMapping("/all")
    public ResponseEntity<List<AnonymizationRule>> getAll() {
        return ResponseEntity.ok(service.getAllRules());
    }

    //  Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<AnonymizationRule> getById(@PathVariable Long id) {
        AnonymizationRule rule = service.getRuleById(id);
        if (rule != null) {
            return ResponseEntity.ok(rule);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //  Get by Data Source ID
    @GetMapping("/by-data-source")
    public ResponseEntity<AnonymizationRule> getByDataSource(@RequestParam String dataSourceId) {
        AnonymizationRule rule = service.getRuleByDataSourceId(dataSourceId);
        if (rule != null) {
            return ResponseEntity.ok(rule);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //  Update
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody AnonymizationRuleDTO request) {
        service.updateRule(id, request);
        return ResponseEntity.ok("Anonymization rule updated successfully!");
    }

    //  Delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        boolean deleted = service.deleteRule(id);
        if (deleted) {
            return ResponseEntity.ok("Anonymization rule deleted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Anonymization rule not found.");
        }
    }
}
