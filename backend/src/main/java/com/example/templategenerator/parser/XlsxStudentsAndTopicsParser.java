package com.example.templategenerator.parser;

import com.example.templategenerator.model.domain.Student;
import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.model.domain.Topic;
import com.example.templategenerator.model.dto.assignment.StudentsAndTopicExcelData;
import com.example.templategenerator.model.templateFields.Item;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class XlsxStudentsAndTopicsParser implements StudentsAndTopicsExcelParser {
    @Override
    public StudentsAndTopicExcelData parse(File excelFile, TemplateType templateType) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            if (workbook.getNumberOfSheets() < 2) {
                throw new IllegalArgumentException("Excel file must contain at least 2 sheets: students and topics.");
            }

            List<Student> students = parseStudents(workbook.getSheetAt(0));
            List<Topic> topics = parseTopics(workbook.getSheetAt(1), templateType);

            if (students.isEmpty()) throw new IllegalArgumentException("Student sheet is empty or invalid.");
            if (topics.isEmpty()) throw new IllegalArgumentException("Topic sheet is empty or invalid.");

            return new StudentsAndTopicExcelData(students, topics);
        }
    }

    private List<Student> parseStudents(Sheet sheet) {
        List<Student> students = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) rowIterator.next(); // Пропустить заголовок

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String group = getCellValue(row.getCell(0));
            String fullName = getCellValue(row.getCell(1));
            if (!group.isBlank() && !fullName.isBlank()) {
                students.add(new Student(group, fullName));
            }
        }
        return students;
    }

    private List<Topic> parseTopics(Sheet sheet, TemplateType templateType) {
        List<Topic> topics = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) rowIterator.next();

        Topic currentTopic = null;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            switch (templateType) {
                case DIPLOMA_PROJECT -> {
                    String title = getCellValue(row.getCell(0));
                    String sourceData = getCellValue(row.getCell(1));
                    if (!title.isBlank()) {
                        Topic topic = new Topic();
                        topic.setType(templateType);
                        topic.setTitle(title);
                        topic.setSourceData(sourceData);
                        topics.add(topic);

                    }
                }
                case COURSE_PROJECT, COURSE_WORK -> {
                    String title = getCellValue(row.getCell(0));

                    if (!title.isBlank()) {
                        currentTopic = new Topic();
                        currentTopic.setType(templateType);
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

        return topics;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                yield (val == Math.floor(val)) ? String.valueOf((int) val) : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}