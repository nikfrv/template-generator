package com.example.templategenerator.model;

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
}
