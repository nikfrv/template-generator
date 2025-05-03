package com.example.templategenerator.service;

import com.example.templategenerator.model.Item;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service("courseProject")
public class CourseProjectTemplateProcessor extends BaseTemplateProcessor {

    @Override
    public byte[] processTemplate(String templateName, Map<String, Object> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IXDocReport report = loadReport(templateName);
            FieldsMetadata metadata = report.createFieldsMetadata();
            IContext context = report.createContext();

            // items
            Object itemsRaw = data.get("items");
            if (itemsRaw instanceof List<?> rawList) {
                List<Item> items = new ArrayList<>();
                for (Object obj : rawList) {
                    if (obj instanceof Map<?, ?> map) {
                        Item item = new Item();
                        item.setName(String.valueOf(map.get("name")));
                        item.setValue(String.valueOf(map.get("value")));
                        items.add(item);
                    }
                }
                metadata.addFieldAsList("items");
                context.put("items", items);
            }

            // Многострочные поля
            handleMultilineFields(context, data, "content", "graphicMaterials", "consultants", "projectStages");

            // Прочие простые поля
            Set<String> skipKeys = Set.of("items", "content", "graphicMaterials", "consultants", "projectStages");
            Map<String, Object> remaining = new HashMap<>(data);
            skipKeys.forEach(remaining::remove);
            putSimpleFields(context, remaining);

            report.process(context, out);
            return out.toByteArray();

        } catch (IOException | XDocReportException e) {
            throw new RuntimeException("Ошибка обработки курсового проекта: " + e.getMessage(), e);
        }
    }

    protected void handleMultilineFields(IContext context, Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object val = data.get(key);
            if (val instanceof String str) {
                // Для корректного переноса строк
                context.put(key, str.replace("\n", "</w:t><w:br/><w:t>"));
            }
        }
    }
}
