package com.example.templategenerator.service.db;

import com.example.templategenerator.entity.GroupEntity;
import com.example.templategenerator.entity.StudentEntity;
import com.example.templategenerator.model.domain.Student;
import com.example.templategenerator.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final GroupService groupService;
    private final StudentRepository studentRepository;

    public void saveAllIfNotExists(List<Student> students) {
        if (students.isEmpty()) return;

        String groupName = students.get(0).getGroup();
        GroupEntity group = groupService.findOrCreateGroup(groupName);

        List<StudentEntity> toSave = students.stream()
                .filter(s -> !studentRepository.existsByFullNameAndGroup_GroupName(s.getFullName(), groupName))
                .map(s -> {
                    StudentEntity entity = new StudentEntity();
                    entity.setFullName(s.getFullName());
                    entity.setGroup(group);
                    return entity;
                })
                .toList();

        studentRepository.saveAll(toSave);
    }
}


