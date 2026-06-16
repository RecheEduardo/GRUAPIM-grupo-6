package com.gruapim.service;

import com.gruapim.domain.entity.Project;
import com.gruapim.domain.entity.ProjectMember;
import com.gruapim.domain.entity.User;
import com.gruapim.domain.enums.UserRole;
import com.gruapim.dto.request.CreateProjectRequest;
import com.gruapim.dto.request.UpdateProjectRequest;
import com.gruapim.dto.response.ProjectResponse;
import com.gruapim.repository.ProjectMemberRepository;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProjectService projectService;

    private User creator;
    private Project project;

    @BeforeEach
    void setUp() {
        creator = User.builder()
                .name("Eduardo Reche")
                .email("eduardo@gruapim.com")
                .password("hashed")
                .role(UserRole.SCRUM_MASTER)
                .build();

        project = Project.builder()
                .name("GRUAPIM")
                .description("Plataforma Scrum")
                .createdBy(creator)
                .build();
    }

    @Test
    void deveCriarProjetoEAdicionarCriadorComoMembro() {
        when(userRepository.findByEmail(creator.getEmail())).thenReturn(Optional.of(creator));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(new ProjectMember());

        ProjectResponse response = projectService.create(
                new CreateProjectRequest("GRUAPIM", "Plataforma Scrum"),
                creator.getEmail()
        );

        assertThat(response.name()).isEqualTo("GRUAPIM");
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoCriar() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.create(
                new CreateProjectRequest("X", null), "inexistente@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveAtualizarProjetoQuandoMembro() {
        UUID projectId = UUID.randomUUID();
        when(userRepository.findByEmail(creator.getEmail())).thenReturn(Optional.of(creator));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, creator.getId())).thenReturn(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.update(
                projectId, new UpdateProjectRequest("Novo Nome", "Nova desc"), creator.getEmail()
        );

        assertThat(response.name()).isEqualTo("Novo Nome");
    }

    @Test
    void deveLancarExcecaoAoAtualizarSemSerMembro() {
        UUID projectId = UUID.randomUUID();
        when(userRepository.findByEmail(creator.getEmail())).thenReturn(Optional.of(creator));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, creator.getId())).thenReturn(false);

        assertThatThrownBy(() -> projectService.update(
                projectId, new UpdateProjectRequest("X", null), creator.getEmail()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Acesso negado: usuário não é membro do projeto");
    }

    @Test
    void deveDeletarProjetoQuandoCriador() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.delete(projectId, creator.getEmail());

        verify(projectRepository).delete(project);
    }

    @Test
    void deveLancarExcecaoAoDeletarSendoOutroUsuario() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(projectId, "outro@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Apenas o criador pode excluir o projeto");
    }
}
