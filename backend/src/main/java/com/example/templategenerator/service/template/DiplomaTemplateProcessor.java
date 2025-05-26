package com.example.templategenerator.service.template;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service("diploma")
public class DiplomaTemplateProcessor extends BaseTemplateProcessor {

    @Override
    public byte[] processTemplate(String fileName, Map<String, Object> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IXDocReport report = loadReport(fileName);
            IContext context = report.createContext();
            putKnownObjects(context, data);
            putSimpleFields(context, data);
            report.process(context, out);
            return out.toByteArray();
        } catch ( XDocReportException | IOException e) {
            throw new RuntimeException("Ошибка обработки дипломного проекта: " + e.getMessage(), e);
        }
    }
}
