package com.gruapim.service;

import com.gruapim.domain.entity.KanbanColumn;
import com.gruapim.domain.entity.Project;
import com.gruapim.dto.request.CreateKanbanColumnRequest;
import com.gruapim.dto.response.KanbanColumnResponse;
import com.gruapim.repository.KanbanColumnRepository;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KanbanService {

    private final KanbanColumnRepository kanbanColumnRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public KanbanColumnResponse createColumn(CreateKanbanColumnRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));

        KanbanColumn column = KanbanColumn.builder()
                .project(project)
                .name(request.name())
                .position(request.position())
                .terminal(request.terminal())
                .build();

        return KanbanColumnResponse.from(kanbanColumnRepository.save(column));
    }

    @Transactional(readOnly = true)
    public List<KanbanColumnResponse> listByProject(UUID projectId) {
        return kanbanColumnRepository.findByProjectIdOrderByPosition(projectId)
                .stream().map(KanbanColumnResponse::from).toList();
    }

    @Transactional
    public void deleteColumn(UUID columnId) {
        KanbanColumn column = kanbanColumnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Coluna não encontrada"));

        if (taskRepository.existsByKanbanColumnId(columnId)) {
            throw new IllegalStateException("Coluna contém tarefas e não pode ser removida");
        }

        kanbanColumnRepository.delete(column);
    }
}