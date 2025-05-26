package com.example.templategenerator.model.dto.assignment;

import com.example.templategenerator.model.domain.Student;
import com.example.templategenerator.model.domain.Topic;
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
