package com.example.templategenerator.controller;

import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.model.domain.Topic;
import com.example.templategenerator.parser.XlsxStudentsAndTopicsParser;
import com.example.templategenerator.service.db.StudentService;
import com.example.templategenerator.service.db.TopicService;
import com.example.templategenerator.service.document.AssignmentDocumentGenerator;
import com.example.templategenerator.service.document.MailService;
import com.example.templategenerator.service.template.TemplateProcessorFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.templategenerator.entity.UserEntity;

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
        private final MailService mailService;

        @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<byte[]> generateDocument(
                        @RequestParam TemplateType type,
                        @RequestParam String fileName,
                        @RequestParam(defaultValue = "false") boolean shuffleTopics,
                        @RequestParam(defaultValue = "docx") String format,
                        @RequestPart MultipartFile excelFile,
                        @RequestPart(required = false) String commonFields) throws Exception {

                Map<String, Object> commonFieldsMap = commonFields != null && !commonFields.isEmpty()
                                ? objectMapper.readValue(commonFields, new TypeReference<Map<String, Object>>() {
                                })
                                : Map.of();

                File tempFile = File.createTempFile("excel", ".xlsx");
                excelFile.transferTo(tempFile);

                var parsed = excelParser.parse(tempFile, type);

                byte[] docx = assignmentDocumentGenerator.generateMergedDocument(
                                parsed.getStudents(),
                                parsed.getTopics(),
                                type,
                                fileName,
                                shuffleTopics,
                                commonFieldsMap);

                if (!tempFile.delete()) {
                        System.err.println("Warning: Failed to delete temp file: " + tempFile.getAbsolutePath());
                }
                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=" + type + "_assignments.docx")
                                .contentType(MediaType.parseMediaType(
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                                .body(docx);
        }

        @PostMapping(value = "/send-to-email", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> sendToEmail(
                        @RequestParam TemplateType type,
                        @RequestParam String fileName,
                        @RequestParam(defaultValue = "false") boolean shuffleTopics,
                        @RequestPart MultipartFile excelFile,
                        @RequestPart(required = false) String commonFields,
                        @AuthenticationPrincipal UserDetails user) throws Exception {
                Map<String, Object> commonFieldsMap = commonFields != null && !commonFields.isEmpty()
                                ? objectMapper.readValue(commonFields, new TypeReference<Map<String, Object>>() {
                                })
                                : Map.of();

                File tempFile = File.createTempFile("excel", ".xlsx");
                excelFile.transferTo(tempFile);

                var parsed = excelParser.parse(tempFile, type);

                byte[] docx = assignmentDocumentGenerator.generateMergedDocument(
                                parsed.getStudents(),
                                parsed.getTopics(),
                                type,
                                fileName,
                                shuffleTopics,
                                commonFieldsMap);

                if (!tempFile.delete()) {
                        System.err.println("Warning: Failed to delete temp file: " + tempFile.getAbsolutePath());
                }

                byte[] file = docx;
                String filename = type + "_assignments.docx";
                String email = ((UserEntity) user).getEmail();
                mailService.sendAssignment(email, file, filename);
                return ResponseEntity.ok(Map.of("message", "Документ отправлен на почту " + email));
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
