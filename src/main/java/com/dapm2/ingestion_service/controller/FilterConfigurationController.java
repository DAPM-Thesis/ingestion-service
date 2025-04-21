package com.dapm2.ingestion_service.controller;
import com.dapm2.ingestion_service.dto.*;
import com.dapm2.ingestion_service.entity.*;
import com.dapm2.ingestion_service.service.StreamConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/config")
@Tag(name = "Stream Configuration API", description = "Manage configurations")
public class StreamConfigurationController {
    private final StreamConfigurationService service;
    public StreamConfigurationController(StreamConfigurationService service) {
        this.service = service;
    }

    @Operation(summary = "Save Attribute", description = "Store the Attributes for specific pipeline into DB")
    @PostMapping("attribute/save")
    public ResponseEntity<String> saveAttributes(@RequestBody AttributeSettingDTO dto) {
        AttributeSetting saved = service.saveAttributes(dto);
        return ResponseEntity.ok("Saved Successfully!!!");
    }
    @Operation(summary = "Get attribute by id", description = "Fetch attributes using its db ID")
    @GetMapping("/attribute/retrieve/{id}")
    public ResponseEntity<AttributeSetting> getAttributeSetting(@PathVariable Long id) {
        AttributeSetting setting = service.getAttributeSettingById(id);
        if (setting != null) {
            return ResponseEntity.ok(setting);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Update attributes", description = "Update attribute for specific source by its db id")
    @PutMapping("/attribute/update/{id}")
    public ResponseEntity<String> updateAttributes(
            @PathVariable Long id, @RequestBody AttributeSettingDTO dto) {
        AttributeSetting updated = service.updateAttributeSetting(id, dto);
        return ResponseEntity.ok("Updated Successfully!!!");
    }
    @Operation(summary = "Change Status of attributes", description = "Update attributes status for specific source by its db id")
    @PostMapping("/attribute/delete/{id}")
    public ResponseEntity<String> updateAttributeStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO dto) {
        boolean result = service.updateAttributeStatus(id, dto.getStatus());
        return result ?
                ResponseEntity.ok("Attribute status updated.") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attribute not found.");
    }
    @Operation(summary = "Delete attributes", description = "Permanently delete attributes from the db for specific source by its db id")
    @DeleteMapping("/attribute/admin/delete/{id}")
    public ResponseEntity<String> deleteAttributes(@PathVariable Long id) {
        boolean deleted = service.deleteAttributeSetting(id);
        if (deleted) {
            return ResponseEntity.ok("Attribute Setting permanently deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attribute setting not found.");
        }
    }
    @Operation(summary = "Save Filter Rules", description = "Store the filters for specific pipeline into DB")
    @PostMapping("/filter/save")
    public ResponseEntity<String> saveFilter(@RequestBody FilterConfigDTO dto) {
        service.saveFilter(dto);
        return ResponseEntity.ok("Filter saved successfully");
    }
    @Operation(summary = "Get filters by id", description = "Fetch filters using its db ID")
    @GetMapping("/filter/retrieve/{id}")
    public ResponseEntity<FilterConfig> getFilter(@PathVariable Long id) {
        FilterConfig config = service.getFilterById(id);
        if (config != null) {
            return ResponseEntity.ok(config);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @Operation(summary = "Update Filter", description = "Update filters for specific source by its db id")
    @PutMapping("/filter/update/{id}")
    public ResponseEntity<String> updateFilter(@PathVariable Long id, @RequestBody FilterConfigDTO dto) {
        service.updateFilter(id, dto);
        return ResponseEntity.ok("Filter updated successfully");
    }
    // Status Change
    @Operation(summary = "Change Status of filters", description = "Update filter status for specific source by its db id")
    @PostMapping("/filter/delete/{id}")
    public ResponseEntity<String> updateFilterStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO dto) {
        boolean result = service.updateFilterStatus(id, dto.getStatus());
        return result ?
                ResponseEntity.ok("Filter status updated.") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Filter not found.");
    }
    @Operation(summary = "Delete filter", description = "Permanently delete filter from the db for specific source by its db id")
    @DeleteMapping("/filter/admin/delete/{id}")
    public ResponseEntity<String> deleteFilter(@PathVariable Long id) {
        boolean deleted = service.deleteFilter(id);
        if (deleted) {
            return ResponseEntity.ok("Filter permanently deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Filter config not found");
        }
    }
}
