package com.example.templategenerator.parser;

import com.example.templategenerator.model.Topic;
import com.example.templategenerator.model.TemplateType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TopicListParser {
    List<Topic> parse(File excelFile, TemplateType templateType) throws IOException;
}
