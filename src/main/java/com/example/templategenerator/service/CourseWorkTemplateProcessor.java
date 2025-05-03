package com.example.templategenerator.service;

import fr.opensagres.xdocreport.template.IContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("courseWork")
public class CourseWorkTemplateProcessor extends CourseProjectTemplateProcessor {
    @Override
    protected void handleMultilineFields(IContext context, Map<String, Object> data, String... keys) {
        super.handleMultilineFields(context, data, "content", "graphicMaterials", "consultants");

    }
}

