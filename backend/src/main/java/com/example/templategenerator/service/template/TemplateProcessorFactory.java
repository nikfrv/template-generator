package com.example.templategenerator.service.template;


import com.example.templategenerator.model.domain.TemplateType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TemplateProcessorFactory {

    private final Map<TemplateType, TemplateProcessor> processors;

    public TemplateProcessorFactory(Map<TemplateType, TemplateProcessor> processors) {
        this.processors = processors;
    }

    public TemplateProcessor getProcessor(TemplateType type) {
        TemplateProcessor processor = processors.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("Unknown template type: " + type);
        }
        return processor;
    }
}
