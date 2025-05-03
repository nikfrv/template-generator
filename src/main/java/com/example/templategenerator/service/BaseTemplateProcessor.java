package com.example.templategenerator.service;

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

    protected IXDocReport loadReport(String templateName) throws IOException, XDocReportException {
        InputStream templateStream = new ClassPathResource("templates/" + templateName).getInputStream();
        return XDocReportRegistry.getRegistry().loadReport(templateStream, TemplateEngineKind.Freemarker);
    }

    protected void closeQuietly(InputStream is) {
        try {
            if (is != null) is.close();
        } catch (IOException ignored) {}
    }
}
