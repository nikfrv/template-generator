/*
package com.example.templategenerator.parser;

import com.example.templategenerator.model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XlsxStudentsAndTopicsParserTest {

    @Test
    void parseDiplomaTemplate_success() throws Exception {
        File file = new File("src/test/resources/unified_diploma.xlsx");
        XlsxStudentsAndTopicsParser parser = new XlsxStudentsAndTopicsParser();

        StudentsAndTopicExcelData data = parser.parse(file, TemplateType.DIPLOMA_PROJECT);

        List<Student> students = data.getStudents();
        List<Topic> topics = data.getTopics();

        assertFalse(students.isEmpty(), "Студенты не должны быть пустыми");
        assertFalse(topics.isEmpty(), "Темы не должны быть пустыми");

        Student firstStudent = students.get(0);
        assertEquals("МС-7", firstStudent.getGroup());
        assertEquals("Иванов Иван Иванович", firstStudent.getFullName());

        Topic firstTopic = topics.get(0);
        assertEquals("Создание бланков для преподавателей", firstTopic.getTitle());
        assertNotNull(firstTopic.getSourceData());
    }

    @Test
    void parseCourseProjectTemplate_success() throws Exception {
        File file = new File("src/test/resources/unified_course_project.xlsx");
        XlsxStudentsAndTopicsParser parser = new XlsxStudentsAndTopicsParser();

        StudentsAndTopicExcelData data = parser.parse(file, TemplateType.COURSE_PROJECT);

        List<Student> students = data.getStudents();
        List<Topic> topics = data.getTopics();

        assertFalse(students.isEmpty());
        assertFalse(topics.isEmpty());

        Student firstStudent = students.get(0);
        assertEquals("МС-7", firstStudent.getGroup());
        assertEquals("Иванов Иван Иванович", firstStudent.getFullName());

        Topic firstTopic = topics.get(0);
        assertEquals("Создание бланков для преподавателей", firstTopic.getTitle());
        assertNotNull(firstTopic.getItems());
        assertEquals(4, firstTopic.getItems().size());

        Item firstItem = firstTopic.getItems().get(0);
        assertEquals("Тест1", firstItem.getName());
        assertEquals("1", firstItem.getValue());
    }

    @Test
    void parseFileWithLessThanTwoSheets_throwsException() {
        File file = new File("src/test/resources/invalid_less_than_two_sheets.xlsx");
        XlsxStudentsAndTopicsParser parser = new XlsxStudentsAndTopicsParser();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(file, TemplateType.DIPLOMA_PROJECT);
        });

        assertTrue(exception.getMessage().contains("at least 2 sheets"));
    }

    @Test
    void parseEmptyStudentsOrTopicsSheet_throwsException() {
        File file = new File("src/test/resources/empty_students_or_topics.xlsx");
        XlsxStudentsAndTopicsParser parser = new XlsxStudentsAndTopicsParser();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(file, TemplateType.DIPLOMA_PROJECT);
        });

        assertTrue(exception.getMessage().contains("empty or invalid"));
    }
}
*/
