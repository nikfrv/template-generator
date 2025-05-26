package com.example.templategenerator.model.dto.assignment;

import com.example.templategenerator.model.domain.TemplateType;
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
