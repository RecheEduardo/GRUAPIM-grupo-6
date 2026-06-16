package com.gruapim.service;

import com.gruapim.domain.entity.Project;
import com.gruapim.domain.entity.User;
import com.gruapim.domain.entity.UserStory;
import com.gruapim.dto.request.CreateUserStoryRequest;
import com.gruapim.dto.request.UpdateUserStoryRequest;
import com.gruapim.dto.response.UserStoryResponse;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.UserRepository;
import com.gruapim.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserStoryResponse create(CreateUserStoryRequest request, String creatorEmail) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        int nextPosition = userStoryRepository.countByProjectId(request.projectId());

        UserStory story = UserStory.builder()
                .project(project)
                .title(request.title())
                .description(request.description())
                .priority(request.priority() != null ? request.priority() : com.gruapim.domain.enums.Priority.MEDIUM)
                .storyPoints(request.storyPoints())
                .position(nextPosition)
                .createdBy(creator)
                .build();

        return UserStoryResponse.from(userStoryRepository.save(story));
    }

    @Transactional(readOnly = true)
    public List<UserStoryResponse> listByProject(UUID projectId) {
        return userStoryRepository.findByProjectIdOrderByPosition(projectId)
                .stream().map(UserStoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserStoryResponse getById(UUID id) {
        return UserStoryResponse.from(findOrThrow(id));
    }

    @Transactional
    public UserStoryResponse update(UUID id, UpdateUserStoryRequest request) {
        UserStory story = findOrThrow(id);
        story.setTitle(request.title());
        story.setDescription(request.description());
        if (request.priority() != null) story.setPriority(request.priority());
        if (request.status() != null) story.setStatus(request.status());
        if (request.storyPoints() != null) story.setStoryPoints(request.storyPoints());
        if (request.position() != null) story.setPosition(request.position());
        return UserStoryResponse.from(userStoryRepository.save(story));
    }

    @Transactional
    public void delete(UUID id) {
        userStoryRepository.delete(findOrThrow(id));
    }

    private UserStory findOrThrow(UUID id) {
        return userStoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User Story não encontrada"));
    }
}