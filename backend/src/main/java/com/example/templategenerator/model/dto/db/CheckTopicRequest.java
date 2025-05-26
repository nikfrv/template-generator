package com.example.templategenerator.model.dto.db;

import com.example.templategenerator.model.domain.TemplateType;

import java.util.List;

public record CheckTopicRequest(List<String> titles, TemplateType type) {
}
