package com.example.templategenerator.parser;

import com.example.templategenerator.model.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XlsxStudentListParser implements StudentListParser {

    @Override
    public List<Student> parse(File excelFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(excelFile)) {
            return parse(fis);
        }
    }

    public List<Student> parse(InputStream inputStream) throws IOException {
        List<Student> students = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // первая вкладка
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // пропускаем заголовок

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String group = getCellValue(row.getCell(0));
                String fullName = getCellValue(row.getCell(1));

                if (!group.isBlank() && !fullName.isBlank()) {
                    students.add(new Student(group, fullName));
                }
            }
        }

        return students;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK, _NONE, ERROR -> "";
        };
    }

}

