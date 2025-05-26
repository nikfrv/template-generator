package com.example.templategenerator.service.template;

import java.util.Map;

public interface TemplateProcessor {
    byte[] processTemplate(String fileName, Map<String, Object> data);
}
