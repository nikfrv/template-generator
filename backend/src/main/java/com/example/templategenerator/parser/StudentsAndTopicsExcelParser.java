package com.example.templategenerator.parser;

import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.model.dto.assignment.StudentsAndTopicExcelData;

import java.io.File;
import java.io.IOException;

public interface StudentsAndTopicsExcelParser {
    StudentsAndTopicExcelData parse(File excelFile, TemplateType templateType) throws IOException;
}
