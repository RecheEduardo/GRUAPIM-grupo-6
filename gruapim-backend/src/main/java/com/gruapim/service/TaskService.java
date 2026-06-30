package com.gruapim.service;

import com.gruapim.domain.entity.*;
import com.gruapim.domain.enums.TaskStatus;
import com.gruapim.dto.request.CreateTaskRequest;
import com.gruapim.dto.request.MoveTaskRequest;
import com.gruapim.dto.request.UpdateTaskRequest;
import com.gruapim.dto.response.TaskResponse;
import com.gruapim.event.DomainEventPublisher;
import com.gruapim.event.TaskAssignedEvent;
import com.gruapim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserStoryRepository userStoryRepository;
    private final UserRepository userRepository;
    private final KanbanColumnRepository kanbanColumnRepository;
    private final TaskStatusHistoryRepository taskStatusHistoryRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public TaskResponse create(CreateTaskRequest request, String creatorEmail) {
        UserStory story = userStoryRepository.findById(request.storyId())
                .orElseThrow(() -> new IllegalArgumentException("User Story não encontrada"));
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Task task = Task.builder()
                .story(story)
                .title(request.title())
                .description(request.description())
                .createdBy(creator)
                .build();

        if (request.assigneeId() != null) {
            task.setAssignee(userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado")));
        }
        if (request.kanbanColumnId() != null) {
            task.setKanbanColumn(kanbanColumnRepository.findById(request.kanbanColumnId())
                    .orElseThrow(() -> new IllegalArgumentException("Coluna Kanban não encontrada")));
        }

        Task saved = taskRepository.save(task);

        if (saved.getAssignee() != null) {
            eventPublisher.publishTaskAssigned(new TaskAssignedEvent(
                    saved.getId(), saved.getTitle(), saved.getAssignee().getId(), creator.getName()));
        }

        return TaskResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByStory(UUID storyId) {
        return taskRepository.findByStoryId(storyId)
                .stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByColumn(UUID columnId) {
        return taskRepository.findByKanbanColumnId(columnId)
                .stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse update(UUID id, UpdateTaskRequest request, String editorEmail) {
        Task task = findOrThrow(id);
        TaskStatus previousStatus = task.getStatus();
        UUID previousAssigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;

        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null && !request.status().equals(previousStatus)) {
            recordStatusChange(task, previousStatus, request.status(), editorEmail);
            task.setStatus(request.status());
        }
        if (request.assigneeId() != null) {
            task.setAssignee(userRepository.findById(request.assigneeId()).orElse(null));
        }
        if (request.kanbanColumnId() != null) {
            task.setKanbanColumn(kanbanColumnRepository.findById(request.kanbanColumnId()).orElse(null));
        }

        Task saved = taskRepository.save(task);

        if (saved.getAssignee() != null && !saved.getAssignee().getId().equals(previousAssigneeId)) {
            String editorName = userRepository.findByEmail(editorEmail).map(User::getName).orElse(editorEmail);
            eventPublisher.publishTaskAssigned(new TaskAssignedEvent(
                    saved.getId(), saved.getTitle(), saved.getAssignee().getId(), editorName));
        }

        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse move(UUID id, MoveTaskRequest request, String editorEmail) {
        Task task = findOrThrow(id);
        KanbanColumn column = kanbanColumnRepository.findById(request.kanbanColumnId())
                .orElseThrow(() -> new IllegalArgumentException("Coluna não encontrada"));

        TaskStatus newStatus = column.isTerminal() ? TaskStatus.DONE : task.getStatus();
        if (!newStatus.equals(task.getStatus())) {
            recordStatusChange(task, task.getStatus(), newStatus, editorEmail);
            task.setStatus(newStatus);
        }
        task.setKanbanColumn(column);

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void delete(UUID id) {
        taskRepository.delete(findOrThrow(id));
    }

    private void recordStatusChange(Task task, TaskStatus from, TaskStatus to, String editorEmail) {
        User editor = userRepository.findByEmail(editorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        taskStatusHistoryRepository.save(TaskStatusHistory.builder()
                .task(task)
                .oldStatus(from)
                .newStatus(to)
                .changedBy(editor)
                .build());
    }

    private Task findOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
    }
}