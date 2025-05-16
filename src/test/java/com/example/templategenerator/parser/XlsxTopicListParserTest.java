package com.example.templategenerator.parser;

import com.example.templategenerator.model.Item;
import com.example.templategenerator.model.Topic;
import com.example.templategenerator.model.TemplateType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XlsxTopicListParserTest {

    @Test
    void parseCourseProjectTopics_success() throws Exception {
        File file = new File("src/test/resources/topics_course_project.xlsx");

        XlsxTopicListParser parser = new XlsxTopicListParser();
        List<Topic> topics = parser.parse(file, TemplateType.COURSE_PROJECT);

        assertEquals(3, topics.size());

        Topic topic = topics.get(0);
        assertEquals("Создание бланков для преподавателей", topic.getTitle());
        assertNotNull(topic.getItems());
        assertEquals(4, topic.getItems().size());

        Item firstItem = topic.getItems().get(0);
        assertEquals("Тест1", firstItem.getName());
        assertEquals("1", firstItem.getValue());
    }

    @Test
    void parseDiplomaTopics_success() throws Exception {
        File file = new File("src/test/resources/topics_diploma.xlsx");

        XlsxTopicListParser parser = new XlsxTopicListParser();
        List<Topic> topics = parser.parse(file, TemplateType.DIPLOMA);

        assertFalse(topics.isEmpty());

        Topic topic = topics.get(0);
        assertEquals("Создание бланков для преподавателей", topic.getTitle());
        assertNotNull(topic.getTitle());
        assertNotNull(topic.getSourceData());
    }
}
