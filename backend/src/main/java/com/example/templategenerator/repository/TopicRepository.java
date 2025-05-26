package com.example.templategenerator.repository;

import com.example.templategenerator.entity.TopicEntity;
import com.example.templategenerator.model.domain.TemplateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    List<TopicEntity> findByTitleAndTypeAndTopicDate(String title, TemplateType type, LocalDate topicDate);
    List<TopicEntity> findByTitleAndType(String title, TemplateType type);
    boolean existsByTitleAndTypeAndTopicDateAfter(String title, TemplateType type, LocalDate dateAfter);
}
