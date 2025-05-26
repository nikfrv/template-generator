package com.example.templategenerator.mapper;

import com.example.templategenerator.entity.TopicEntity;
import com.example.templategenerator.model.domain.Topic;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TopicMapper {

    public TopicEntity toEntity(Topic topic) {
        TopicEntity entity = new TopicEntity();
        entity.setTitle(topic.getTitle());
        entity.setType(topic.getType());
        entity.setTopicDate(LocalDate.now());
        return entity;
    }


    public List<TopicEntity> toEntityList(List<Topic> topics) {
        return topics.stream()
                .map(this::toEntity)
                .toList();
    }
}

