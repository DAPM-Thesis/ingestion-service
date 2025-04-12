package com.dapm2.ingestion_service.controller;
import com.dapm2.ingestion_service.dto.*;
import com.dapm2.ingestion_service.entity.*;
import com.dapm2.ingestion_service.service.StreamConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/config")
public class StreamConfigurationController {
    private final StreamConfigurationService service;
    public StreamConfigurationController(StreamConfigurationService service) {
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
    @PostMapping("/filter/save")
    public ResponseEntity<String> saveFilter(@RequestBody FilterConfigDTO dto) {
        service.saveFilter(dto);
        return ResponseEntity.ok("Filter saved successfully");
    }
    @GetMapping("/filter/{id}")
    public ResponseEntity<FilterConfig> getFilter(@PathVariable Long id) {
        FilterConfig config = service.getFilterById(id);
        if (config != null) {
            return ResponseEntity.ok(config);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PutMapping("/filter/update/{id}")
    public ResponseEntity<String> updateFilter(@PathVariable Long id, @RequestBody FilterConfigDTO dto) {
        service.updateFilter(id, dto);
        return ResponseEntity.ok("Filter updated successfully");
    }
    @DeleteMapping("/filter/delete/{id}")
    public ResponseEntity<String> deleteFilter(@PathVariable Long id) {
        boolean deleted = service.deleteFilter(id);
        if (deleted) {
            return ResponseEntity.ok("Filter deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Filter config not found");
        }
    }
}
