package com.example.templategenerator.parser;

import com.example.templategenerator.model.TemplateType;
import com.example.templategenerator.model.StudentsAndTopicExcelData;

import java.io.File;
import java.io.IOException;

public interface StudentsAndTopicsExcelParser {
    StudentsAndTopicExcelData parse(File excelFile, TemplateType templateType) throws IOException;
}
