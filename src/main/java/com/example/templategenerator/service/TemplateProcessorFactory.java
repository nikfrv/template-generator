package com.example.templategenerator.service;


import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TemplateProcessorFactory {

    private final Map<String, TemplateProcessor> processors;

    public TemplateProcessorFactory(Map<String, TemplateProcessor> processors) {
        this.processors = processors;
    }

    public TemplateProcessor getProcessor(String type) {
        TemplateProcessor processor = processors.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("Unknown template type: " + type);
        }
        return processor;
    }
}

