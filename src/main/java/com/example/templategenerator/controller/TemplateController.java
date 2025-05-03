package com.example.templategenerator.controller;

import com.example.templategenerator.service.TemplateProcessor;
import com.example.templategenerator.service.TemplateProcessorFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateProcessorFactory templateProcessorFactory;

    public TemplateController(TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateTemplate(
            @RequestParam String type,
            @RequestParam String templateName,
            @RequestBody Map<String, Object> data) {

        TemplateProcessor processor = templateProcessorFactory.getProcessor(type);
        byte[] document = processor.processTemplate(templateName, data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + templateName)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(document);
    }
}
