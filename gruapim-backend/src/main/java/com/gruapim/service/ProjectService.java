package com.gruapim.service;

import com.gruapim.domain.entity.Project;
import com.gruapim.domain.entity.ProjectMember;
import com.gruapim.domain.entity.User;
import com.gruapim.domain.enums.UserRole;
import com.gruapim.dto.request.AddMemberRequest;
import com.gruapim.dto.request.CreateProjectRequest;
import com.gruapim.dto.request.UpdateProjectRequest;
import com.gruapim.dto.response.ProjectMemberResponse;
import com.gruapim.dto.response.ProjectResponse;
import com.gruapim.repository.ProjectMemberRepository;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse create(CreateProjectRequest request, String creatorEmail) {
        User creator = findUserByEmail(creatorEmail);

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .createdBy(creator)
                .build();

        Project saved = projectRepository.save(project);

        projectMemberRepository.save(ProjectMember.builder()
                .project(saved)
                .user(creator)
                .role(UserRole.SCRUM_MASTER)
                .build());

        return ProjectResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listForUser(String email) {
        User user = findUserByEmail(email);
        return projectRepository.findAllByMemberId(user.getId())
                .stream().map(ProjectResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        return ProjectResponse.from(findOrThrow(id));
    }

    @Transactional
    public ProjectResponse update(UUID id, UpdateProjectRequest request, String requesterEmail) {
        assertMember(id, requesterEmail);
        Project project = findOrThrow(id);
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void delete(UUID id, String requesterEmail) {
        Project project = findOrThrow(id);
        if (!project.getCreatedBy().getEmail().equals(requesterEmail)) {
            throw new IllegalArgumentException("Apenas o criador pode excluir o projeto");
        }
        projectRepository.delete(project);
    }

    @Transactional
    public ProjectMemberResponse addMember(UUID projectId, AddMemberRequest request, String requesterEmail) {
        assertMember(projectId, requesterEmail);
        Project project = findOrThrow(projectId);
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, request.userId())) {
            throw new IllegalStateException("Usuário já é membro deste projeto");
        }

        return ProjectMemberResponse.from(projectMemberRepository.save(
                ProjectMember.builder()
                        .project(project)
                        .user(user)
                        .role(request.role())
                        .build()
        ));
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> listMembers(UUID projectId) {
        return projectMemberRepository.findByProjectId(projectId)
                .stream().map(ProjectMemberResponse::from).toList();
    }

    @Transactional
    public void removeMember(UUID projectId, UUID userId, String requesterEmail) {
        Project project = findOrThrow(projectId);
        if (!project.getCreatedBy().getEmail().equals(requesterEmail)) {
            throw new IllegalArgumentException("Apenas o criador pode remover membros");
        }
        projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado no projeto"));
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    private Project findOrThrow(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    private void assertMember(UUID projectId, String email) {
        User user = findUserByEmail(email);
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new IllegalArgumentException("Acesso negado: usuário não é membro do projeto");
        }
    }
}
