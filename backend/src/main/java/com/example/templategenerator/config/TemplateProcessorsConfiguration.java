package com.example.templategenerator.config;

import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.service.template.CourseProjectTemplateProcessor;
import com.example.templategenerator.service.template.DiplomaTemplateProcessor;
import com.example.templategenerator.service.template.TemplateProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TemplateProcessorsConfiguration {

    @Bean
    public Map<TemplateType, TemplateProcessor> templateProcessors(
            @Qualifier("courseProject") CourseProjectTemplateProcessor courseProject,
            @Qualifier("courseWork") CourseProjectTemplateProcessor courseWork,
            DiplomaTemplateProcessor diploma
    ) {
        return Map.of(
                TemplateType.COURSE_PROJECT, courseProject,
                TemplateType.COURSE_WORK, courseWork,
                TemplateType.DIPLOMA_PROJECT, diploma
        );
    }
}
