package com.dapm2.ingestion_service.controller;

import com.dapm2.ingestion_service.dto.AttributeSettingDTO;
import com.dapm2.ingestion_service.entity.AttributeSetting;
import com.dapm2.ingestion_service.service.ConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/config")
public class ConfigurationController {
    private final ConfigurationService service;

    public ConfigurationController(ConfigurationService service) {
        this.service = service;
    }

    @PostMapping("attribute/save")
    public ResponseEntity<String> saveAttributes(@RequestBody AttributeSettingDTO dto) {
        AttributeSetting saved = service.saveAttributes(dto);
        return ResponseEntity.ok("Saved Successfully!!!");
    }

    @GetMapping("/attribute/{id}")
    public ResponseEntity<AttributeSetting> getAttributeSetting(@PathVariable Long id) {
        AttributeSetting setting = service.getAttributeSettingById(id);
        if (setting != null) {
            return ResponseEntity.ok(setting);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/attribute/update/{id}")
    public ResponseEntity<String> updateAttributes(
            @PathVariable Long id, @RequestBody AttributeSettingDTO dto) {
        AttributeSetting updated = service.updateAttributeSetting(id, dto);
        return ResponseEntity.ok("Updated Successfully!!!");
    }

    @DeleteMapping("/attribute/delete/{id}")
    public ResponseEntity<String> deleteAttributes(@PathVariable Long id) {
        boolean deleted = service.deleteAttributeSetting(id);
        if (deleted) {
            return ResponseEntity.ok("Deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attribute setting not found.");
        }
    }

}
