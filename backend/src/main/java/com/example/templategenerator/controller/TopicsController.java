package com.example.templategenerator.controller;

import com.example.templategenerator.model.domain.TemplateType;
import com.example.templategenerator.service.db.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicsController {

    private final TopicService topicService;

    @GetMapping("/check-unique")
    public ResponseEntity<?> checkTopicUnique(
            @RequestParam String title,
            @RequestParam TemplateType type
    ) {
        boolean exists = topicService.existsByTitleAndTypeSince(title, type, 5);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

}
