package com.example.templategenerator.parser;

import com.example.templategenerator.model.Student;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XlsxStudentListParserTest {

    @Test
    void testParse() throws Exception {
        XlsxStudentListParser parser = new XlsxStudentListParser();

        try (InputStream is = getClass().getResourceAsStream("/students.xlsx")) {
            assertNotNull(is, "Файл students.xlsx не найден в resources");

            List<Student> students = parser.parse(is);

            assertFalse(students.isEmpty(), "Список студентов не должен быть пустым");

            Student first = students.get(0);
            assertEquals("МС-7", first.getGroup());
            assertEquals("Иванов Иван Иванович", first.getFullName());
        }
    }
}
