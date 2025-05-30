package com.example.templategenerator.service.template;

import com.example.templategenerator.model.templateFields.Consultant;
import com.example.templategenerator.model.templateFields.Content;
import com.example.templategenerator.model.templateFields.Item;
import com.example.templategenerator.model.templateFields.ProjectStage;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("courseWork")
public class CourseWorkTemplateProcessor extends CourseProjectTemplateProcessor {
    @Override
    public byte[] processTemplate(String fileName, Map<String, Object> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            IXDocReport report = loadReport(fileName);


            FieldsMetadata metadata = report.createFieldsMetadata();
            metadata.load("content", Content.class, true);
            metadata.load("consultants", Consultant.class, true);
            metadata.load("projectStage", ProjectStage.class, true);
            metadata.load("topic.items", Item.class, true);

            IContext context = report.createContext();

            putKnownObjects(context, data);

            handleMultilineAsValueObjects(context, data, Map.of(
                    "content", Content::new,
                    "consultants", Consultant::new,
                    "projectStage", ProjectStage::new
            ));

            // Добавляем остальные поля как простые
            Map<String, Object> filteredData = new HashMap<>(data);
            List.of("content", "consultants", "projectStage").forEach(filteredData::remove);
            putSimpleFields(context, filteredData);

            report.process(context, out);
            return out.toByteArray();

        } catch (IOException | XDocReportException e) {
            throw new RuntimeException("Ошибка обработки курсового проекта: " + e.getMessage(), e);
        }
    }
}
