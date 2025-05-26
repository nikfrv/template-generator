package com.example.templategenerator.repository;

import com.example.templategenerator.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> findByFullName(String fullName);
    boolean existsByFullNameAndGroup_GroupName(String fullName, String groupGroupName);
    List<StudentEntity> findAllByGroup_GroupName(String groupGroupName);
}

