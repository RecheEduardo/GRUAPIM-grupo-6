package com.gruapim.service;

import com.gruapim.domain.entity.Project;
import com.gruapim.domain.entity.Sprint;
import com.gruapim.domain.entity.SprintStory;
import com.gruapim.domain.entity.UserStory;
import com.gruapim.domain.enums.SprintStatus;
import com.gruapim.dto.request.AddStoryToSprintRequest;
import com.gruapim.dto.request.CreateSprintRequest;
import com.gruapim.dto.request.UpdateSprintRequest;
import com.gruapim.dto.response.SprintResponse;
import com.gruapim.dto.response.UserStoryResponse;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.SprintRepository;
import com.gruapim.repository.SprintStoryRepository;
import com.gruapim.repository.UserRepository;
import com.gruapim.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SprintStoryRepository sprintStoryRepository;
    private final UserStoryRepository userStoryRepository;

    @Transactional
    public SprintResponse create(CreateSprintRequest request, String creatorEmail) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));

        boolean hasInProgress = sprintRepository.existsByProjectIdAndStatus(request.projectId(), SprintStatus.IN_PROGRESS);
        if (hasInProgress) {
            throw new IllegalStateException("Já existe um sprint em andamento neste projeto");
        }

        Sprint sprint = Sprint.builder()
                .project(project)
                .name(request.name())
                .goal(request.goal())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .createdBy(userRepository.findByEmail(creatorEmail)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado")))
                .build();

        return SprintResponse.from(sprintRepository.save(sprint));
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> listByProject(UUID projectId) {
        return sprintRepository.findByProjectIdOrderByStartDate(projectId)
                .stream().map(SprintResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SprintResponse getById(UUID id) {
        return SprintResponse.from(findOrThrow(id));
    }

    @Transactional
    public SprintResponse update(UUID id, UpdateSprintRequest request) {
        Sprint sprint = findOrThrow(id);
        sprint.setName(request.name());
        sprint.setGoal(request.goal());
        sprint.setStartDate(request.startDate());
        sprint.setEndDate(request.endDate());
        if (request.status() != null) sprint.setStatus(request.status());
        return SprintResponse.from(sprintRepository.save(sprint));
    }

    @Transactional
    public void addStory(UUID sprintId, AddStoryToSprintRequest request) {
        Sprint sprint = findOrThrow(sprintId);
        UserStory story = userStoryRepository.findById(request.storyId())
                .orElseThrow(() -> new IllegalArgumentException("User Story não encontrada"));

        if (sprintStoryRepository.existsBySprintIdAndStoryId(sprintId, request.storyId())) {
            throw new IllegalStateException("Story já adicionada a este sprint");
        }

        sprintStoryRepository.save(SprintStory.builder().sprint(sprint).story(story).build());
    }

    @Transactional(readOnly = true)
    public List<UserStoryResponse> listStories(UUID sprintId) {
        return sprintStoryRepository.findBySprintId(sprintId)
                .stream().map(ss -> UserStoryResponse.from(ss.getStory())).toList();
    }

    @Transactional
    public void removeStory(UUID sprintId, UUID storyId) {
        SprintStory ss = sprintStoryRepository.findBySprintIdAndStoryId(sprintId, storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story não encontrada neste sprint"));
        sprintStoryRepository.delete(ss);
    }

    private Sprint findOrThrow(UUID id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sprint não encontrado"));
    }
}