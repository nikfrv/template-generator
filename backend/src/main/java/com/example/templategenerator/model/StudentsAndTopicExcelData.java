package com.example.templategenerator.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StudentsAndTopicExcelData {
    private List<Student> students;
    private List<Topic> topics;
}
