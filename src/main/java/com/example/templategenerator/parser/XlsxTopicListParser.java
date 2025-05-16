package com.example.templategenerator.parser;

import com.example.templategenerator.model.Item;
import com.example.templategenerator.model.Topic;
import com.example.templategenerator.model.TemplateType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XlsxTopicListParser implements TopicListParser {
    @Override
    public List<Topic> parse(File excelFile, TemplateType templateType) throws IOException {
        List<Topic> topics = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // пропускаем заголовок

            Topic currentTopic = null;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                switch (templateType) {
                    case DIPLOMA -> {
                        String title = getCellValue(row.getCell(0));
                        String sourceData = getCellValue(row.getCell(1));
                        if (!title.isBlank()) {
                            Topic topic = new Topic();
                            topic.setTitle(title);
                            topic.setSourceData(sourceData);
                            topics.add(topic);
                        }
                    }
                    case COURSE_PROJECT, COURSE_WORK -> {
                        String title = getCellValue(row.getCell(0));

                        if (!title.isBlank()) {
                            currentTopic = new Topic();
                            currentTopic.setTitle(title);
                            currentTopic.setItems(new ArrayList<>());
                            topics.add(currentTopic);
                        }

                        if (currentTopic != null) {
                            String name = getCellValue(row.getCell(1));
                            String value = getCellValue(row.getCell(2));
                            if (!name.isBlank() || !value.isBlank()) {
                                Item item = new Item();
                                item.setName(name);
                                item.setValue(value);
                                currentTopic.getItems().add(item);
                            }
                        }
                    }
                }
            }
        }

        return topics;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                // Убираем .0, если это целое
                yield (val == Math.floor(val)) ? String.valueOf((int) val) : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK, _NONE, ERROR -> "";
        };
    }
}

