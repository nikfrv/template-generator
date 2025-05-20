package com.example.templategenerator.controller;

import com.example.templategenerator.model.AssignmentGenerationRequest;
import com.example.templategenerator.model.StudentsAndTopicExcelData;
import com.example.templategenerator.model.TemplateType;
import com.example.templategenerator.parser.XlsxStudentsAndTopicsParser;
import com.example.templategenerator.service.AssignmentDocumentGenerator;
import com.example.templategenerator.service.TemplateProcessor;
import com.example.templategenerator.service.TemplateProcessorFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TemplateProcessorFactory templateProcessorFactory;
    private final AssignmentDocumentGenerator assignmentDocumentGenerator;
    private final XlsxStudentsAndTopicsParser excelParser;

    public TemplateController(TemplateProcessorFactory templateProcessorFactory,
                              AssignmentDocumentGenerator assignmentDocumentGenerator,
                              XlsxStudentsAndTopicsParser excelParser) {
        this.templateProcessorFactory = templateProcessorFactory;
        this.assignmentDocumentGenerator = assignmentDocumentGenerator;
        this.excelParser = excelParser;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateTemplate(
            @RequestParam TemplateType type,
            @RequestParam String fileName,
            @RequestBody Map<String, Object> data) {

        TemplateProcessor processor = templateProcessorFactory.getProcessor(type);
        byte[] document = processor.processTemplate(fileName, data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(document);
    }

    @PostMapping("/generate/bulk")
    public ResponseEntity<byte[]> generateBulkFromJson(@RequestBody AssignmentGenerationRequest request) {
        byte[] document = assignmentDocumentGenerator.generateMergedDocument(
                request.getExcelData().getStudents(),
                request.getExcelData().getTopics(),
                request.getTemplateType(),
                request.getFileName(),
                request.isShuffleTopics(),
                request.getCommonFields()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=assignments.docx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(document);
    }

    @PostMapping(value = "/generate/from-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> generateFromExcel(
            @RequestParam TemplateType type,
            @RequestParam String fileName,
            @RequestParam(defaultValue = "false") boolean shuffleTopics,
            @RequestPart("excelFile") MultipartFile excelFile,
            @RequestPart(value = "commonFields", required = false) String commonFieldsJson
    ) throws IOException {

        Map<String, Object> commonFields = null;
        if (commonFieldsJson != null) {
            commonFields = objectMapper.readValue(commonFieldsJson, new TypeReference<>() {});
        }

        System.out.println("commonFields keys: " + (commonFields == null ? "null" : commonFields.keySet()));

        // Сохраняем MultipartFile во временный файл
        File tempFile = File.createTempFile("excel", ".xlsx");
        excelFile.transferTo(tempFile);

        // Парсим студентов и темы
        StudentsAndTopicExcelData parsed = excelParser.parse(tempFile, type);

        // Генерация документа
        byte[] document = assignmentDocumentGenerator.generateMergedDocument(
                parsed.getStudents(),
                parsed.getTopics(),
                type,
                fileName,
                shuffleTopics,
                commonFields
        );

        boolean deleted = tempFile.delete();
        if (!deleted) {
            System.err.println("Warning: Temporary file could not be deleted: " + tempFile.getAbsolutePath());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+ type +"_assignments.docx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(document);
    }
}
