package com.example.templategenerator.service;

import java.util.Map;

public interface TemplateProcessor {
    byte[] processTemplate(String templateName, Map<String, Object> data);
}
