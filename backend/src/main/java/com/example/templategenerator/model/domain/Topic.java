package com.example.templategenerator.model.domain;

import com.example.templategenerator.model.templateFields.Item;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Topic {
    private String title;
    private List<Item> items;
    private String sourceData;
    private TemplateType type;

    public Topic(String title, String sourceData, TemplateType type) {
        this.title = title;
        this.items = null;
        this.sourceData = sourceData;
        this.type = type;
    }
}
