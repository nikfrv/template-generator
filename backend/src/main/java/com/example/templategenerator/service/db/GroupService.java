package com.example.templategenerator.service.db;

import com.example.templategenerator.entity.GroupEntity;
import com.example.templategenerator.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupEntity findOrCreateGroup(String groupName) {
        return groupRepository.findByGroupName(groupName)
                .orElseGet(() -> groupRepository.save(new GroupEntity(groupName)));
    }

    public Optional<GroupEntity> getGroupByName(String groupName) {
        return groupRepository.findByGroupName(groupName);
    }
}
