package com.example.templategenerator.service.template;

import com.example.templategenerator.model.domain.Student;
import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.model.domain.Topic;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateDataFactory {

    public Map<String, Object> build(Student student, Topic topic, TemplateType templateType) {
        Map<String, Object> data = new HashMap<>();

        data.put("student", student);

        if (templateType == TemplateType.DIPLOMA_PROJECT) {
            data.put("topic", new Topic(topic.getTitle(), topic.getSourceData(), templateType));
            data.put("sourceData", topic.getSourceData());
        } else {
            data.put("topic", topic);
        }

        return data;
    }
}
