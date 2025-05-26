package com.example.templategenerator.service.document;

import com.example.templategenerator.model.domain.Student;
import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.model.domain.Topic;
import com.example.templategenerator.service.template.TemplateDataFactory;
import com.example.templategenerator.service.template.TemplateProcessor;
import com.example.templategenerator.service.template.TemplateProcessorFactory;
import com.example.templategenerator.util.asignment.DocxMergeUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssignmentDocumentGenerator {

    private final TemplateProcessorFactory templateProcessorFactory;
    private final TemplateDataFactory dataFactory;

    public AssignmentDocumentGenerator(
            TemplateProcessorFactory templateProcessorFactory,
            TemplateDataFactory dataFactory
    ) {
        this.templateProcessorFactory = templateProcessorFactory;
        this.dataFactory = dataFactory;
    }

    public byte[] generateMergedDocument(
            List<Student> students,
            List<Topic> topics,
            TemplateType templateType,
            String templateName,
            boolean shuffleTopics,
            Map<String, Object> commonFields
    ) {
        if (topics.size() < students.size()) {
            throw new IllegalArgumentException("Тем меньше, чем студентов");
        }

        List<Topic> topicList = new ArrayList<>(topics);
        if (shuffleTopics) {
            Collections.shuffle(topicList);
        }

        Map<Student, Topic> assignment = new LinkedHashMap<>();
        for (int i = 0; i < students.size(); i++) {
            assignment.put(students.get(i), topicList.get(i));
        }

        TemplateProcessor processor = templateProcessorFactory.getProcessor(templateType);
        List<byte[]> generatedDocs = new ArrayList<>();

        for (Map.Entry<Student, Topic> entry : assignment.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            if (commonFields != null) {
                data.putAll(commonFields);
            }
            data.putAll(dataFactory.build(entry.getKey(), entry.getValue(), templateType));
            byte[] doc = processor.processTemplate(templateName, data);
            generatedDocs.add(doc);
        }


        try {
            return DocxMergeUtil.mergeDocuments(generatedDocs);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при объединении документов", e);
        }
    }
}
