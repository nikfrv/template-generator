package com.example.templategenerator.repository;

import com.example.templategenerator.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    boolean existsByGroupName(String groupName);
    Optional<GroupEntity> findByGroupName(String groupName);
}
