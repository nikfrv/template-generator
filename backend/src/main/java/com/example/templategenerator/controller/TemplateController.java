package com.example.templategenerator.controller;

import com.example.templategenerator.model.domain.Topic;
import com.example.templategenerator.model.dto.assignment.AssignmentGenerationRequest;
import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.parser.XlsxStudentsAndTopicsParser;
import com.example.templategenerator.service.document.AssignmentDocumentGenerator;
import com.example.templategenerator.service.db.StudentService;
import com.example.templategenerator.service.db.TopicService;
import com.example.templategenerator.service.template.TemplateProcessorFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateProcessorFactory templateProcessorFactory;
    private final AssignmentDocumentGenerator assignmentDocumentGenerator;
    private final XlsxStudentsAndTopicsParser excelParser;
    private final ObjectMapper objectMapper;
    private final StudentService studentService;
    private final TopicService topicService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateTemplate(
            @RequestParam TemplateType type,
            @RequestParam String fileName,
            @RequestBody Map<String, Object> data) {

        var processor = templateProcessorFactory.getProcessor(type);
        byte[] document = processor.processTemplate(fileName, data);

        return buildResponse(document, fileName);
    }

    @PostMapping("/generate/bulk")
    public ResponseEntity<byte[]> generateBulkFromJson(@RequestBody AssignmentGenerationRequest request) {
        byte[] document = assignmentDocumentGenerator.generateMergedDocument(
                request.getExcelData().getStudents(),
                request.getExcelData().getTopics(),
                request.getTemplateType(),
                request.getFileName(),
                request.isShuffleTopics(),
                request.getCommonFields());
        return buildResponse(document, "assignments.docx");
    }

    @PostMapping(value = "/generate/from-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> generateFromExcel(
            @RequestParam TemplateType type,
            @RequestParam String fileName,
            @RequestParam(defaultValue = "false") boolean shuffleTopics,
            @RequestPart MultipartFile excelFile,
            @RequestPart(required = false) String commonFields) throws IOException {

        Map<String, Object> commonFieldsMap = (Map<String, Object>) (Map<?, ?>) objectMapper.readValue(commonFields,
                new TypeReference<Map<String, Object>>() {
                });

        File tempFile = File.createTempFile("excel", ".xlsx");
        excelFile.transferTo(tempFile);

        var parsed = excelParser.parse(tempFile, type);

        byte[] document = assignmentDocumentGenerator.generateMergedDocument(
                parsed.getStudents(),
                parsed.getTopics(),
                type,
                fileName,
                shuffleTopics,
                commonFieldsMap);

        if (!tempFile.delete()) {
            System.err.println("Warning: Failed to delete temp file: " + tempFile.getAbsolutePath());
        }

        return buildResponse(document, type + "_assignments.docx");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] document, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(document);
    }

    @PostMapping(value = "/upload/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExcelAndPersistData(
            @RequestParam TemplateType type,
            @RequestPart MultipartFile excelFile) throws IOException {
        File tempFile = File.createTempFile("excel", ".xlsx");
        excelFile.transferTo(tempFile);

        var parsed = excelParser.parse(tempFile, type);

        studentService.saveAllIfNotExists(parsed.getStudents());
        topicService.saveAllIfNotExists(parsed.getTopics());

        if (!tempFile.delete()) {
            System.err.println("Warning: Failed to delete temp file: " + tempFile.getAbsolutePath());
        }

        return ResponseEntity.ok("Data loaded to database.");
    }

    @PostMapping(value = "/check-duplicates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> checkDuplicates(
            @RequestParam TemplateType type,
            @RequestPart MultipartFile excelFile) throws IOException {
        File tempFile = File.createTempFile("excel", ".xlsx");
        excelFile.transferTo(tempFile);

        var parsed = excelParser.parse(tempFile, type);
        List<String> titles = parsed.getTopics().stream()
                .map(Topic::getTitle)
                .toList();

        List<String> duplicates = titles.stream()
                .filter(title -> topicService.existsByTitleAndTypeSince(title, type, 5))
                .toList();

        if (!tempFile.delete()) {
            System.err.println("Warning: Failed to delete temp file: " + tempFile.getAbsolutePath());
        }

        return ResponseEntity.ok(Map.of("duplicates", duplicates));
    }
}
