package com.example.templategenerator.service;

import com.example.templategenerator.model.*;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@Service("courseProject")
public class CourseProjectTemplateProcessor extends BaseTemplateProcessor {

    @Override
    public byte[] processTemplate(String templateName, Map<String, Object> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            IXDocReport report = loadReport(templateName);


            FieldsMetadata metadata = report.createFieldsMetadata();
            metadata.load("content", Content.class, true);
            metadata.load("graphic", GraphicMaterial.class, true);
            metadata.load("consultants", Consultant.class, true);
            metadata.load("projectStage", ProjectStage.class, true);
            metadata.load("topic.items", Item.class, true);

            IContext context = report.createContext();

            putKnownObjects(context, data);

            handleMultilineAsValueObjects(context, data, Map.of(
                    "content", Content::new,
                    "graphic", GraphicMaterial::new,
                    "consultants", Consultant::new,
                    "projectStage", ProjectStage::new
            ));

            // Добавляем остальные поля как простые
            Map<String, Object> filteredData = new HashMap<>(data);
            List.of("content", "graphic", "consultants", "projectStage").forEach(filteredData::remove);
            putSimpleFields(context, filteredData);

            report.process(context, out);
            return out.toByteArray();

        } catch (IOException | XDocReportException e) {
            throw new RuntimeException("Ошибка обработки курсового проекта: " + e.getMessage(), e);
        }
    }

    protected <T> void handleMultilineAsValueObjects(
            IContext context,
            Map<String, Object> data,
            Map<String, Function<String, T>> keyToConstructor
    ) {
        for (Map.Entry<String, Function<String, T>> entry : keyToConstructor.entrySet()) {
            String key = entry.getKey();
            Object raw = data.get(key);
            if (raw instanceof String rawString) {
                String correctedStr = rawString.replace("\\n", "\n");
                List<T> values = Arrays.stream(correctedStr.split("\\R"))
                        .map(String::trim)
                        .filter(line -> !line.isEmpty())
                        .map(entry.getValue())
                        .toList();

                context.put(key, values);
            }
        }
    }

}
