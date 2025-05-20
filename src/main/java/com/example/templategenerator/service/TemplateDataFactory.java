package com.example.templategenerator.service;

import com.example.templategenerator.model.Student;
import com.example.templategenerator.model.TemplateType;
import com.example.templategenerator.model.Topic;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateDataFactory {

    public Map<String, Object> build(Student student, Topic topic, TemplateType templateType) {
        Map<String, Object> data = new HashMap<>();

        data.put("student", student);

        if (templateType == TemplateType.DIPLOMA) {
            data.put("topic", new Topic(topic.getTitle(), null, topic.getSourceData()));
            data.put("sourceData", topic.getSourceData());
        } else {
            data.put("topic", topic);
        }

        return data;
    }
}

