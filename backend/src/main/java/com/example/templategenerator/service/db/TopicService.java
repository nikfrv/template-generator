package com.example.templategenerator.service.db;

import com.example.templategenerator.entity.TopicEntity;
import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.model.domain.Topic;
import com.example.templategenerator.mapper.TopicMapper;
import com.example.templategenerator.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    public void save(Topic topic) {
        TopicEntity entity = topicMapper.toEntity(topic);
        topicRepository.save(entity);
    }

    public List<TopicEntity> findSimilarTopics(String title, TemplateType templateType, int yearsBack) {
        LocalDate dateAfter = LocalDate.now().minusYears(yearsBack);
        return topicRepository.findByTitleAndTypeAndTopicDate(title, templateType, dateAfter);
    }

    public void saveAllIfNotExists(List<Topic> topics) {
        if (topics.isEmpty()) return;

        List<TopicEntity> toSave = topics.stream()
                .filter(topic -> topicRepository.findByTitleAndType(
                        topic.getTitle(),
                        topic.getType()
                ).isEmpty())
                .map(topicMapper::toEntity)
                .toList();

        topicRepository.saveAll(toSave);
    }
}


