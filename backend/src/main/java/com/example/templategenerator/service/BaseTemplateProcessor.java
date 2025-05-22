package com.example.templategenerator.service;

import com.example.templategenerator.model.Student;
import com.example.templategenerator.model.Topic;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class BaseTemplateProcessor implements TemplateProcessor {

    protected void putSimpleFields(IContext context, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String[] arr) {
                context.put(entry.getKey(), arr[0]);
            } else {
                context.put(entry.getKey(), value);
            }
        }
    }

        protected void putKnownObjects(IContext context, Map<String, Object> data) {
            if (data.get("student") instanceof Student student) {
                context.put("student", student);
            }
            if (data.get("topic") instanceof Topic topic) {
                context.put("topic", topic);
            }
        }


    protected IXDocReport loadReport(String fileName) throws IOException, XDocReportException {
        InputStream templateStream = new ClassPathResource("templates/" + fileName).getInputStream();
        return XDocReportRegistry.getRegistry().loadReport(templateStream, TemplateEngineKind.Freemarker);
    }
}
