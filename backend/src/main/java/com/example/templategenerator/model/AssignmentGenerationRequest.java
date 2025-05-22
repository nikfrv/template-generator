package com.example.templategenerator.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentGenerationRequest {

    private StudentsAndTopicExcelData excelData;
    private Map<String, Object> commonFields;
    private TemplateType templateType;
    private String fileName;
    private boolean shuffleTopics;
}
