package com.gruapim.service;

import com.gruapim.domain.entity.GitCommitLink;
import com.gruapim.domain.entity.GitRepository;
import com.gruapim.domain.entity.Project;
import com.gruapim.domain.entity.Task;
import com.gruapim.domain.entity.User;
import com.gruapim.domain.enums.UserRole;
import com.gruapim.dto.request.LinkCommitRequest;
import com.gruapim.dto.request.RegisterGitRepoRequest;
import com.gruapim.dto.response.GitCommitLinkResponse;
import com.gruapim.dto.response.GitRepoResponse;
import com.gruapim.repository.GitCommitLinkRepository;
import com.gruapim.repository.GitRepoRepository;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.TaskRepository;
import com.gruapim.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitServiceTest {

    @Mock private GitRepoRepository gitRepoRepository;
    @Mock private GitCommitLinkRepository gitCommitLinkRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private GitService gitService;

    private User user;
    private Project project;
    private GitRepository gitRepo;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("João Ramos")
                .email("joao@gruapim.com")
                .password("hashed")
                .role(UserRole.DEVELOPER)
                .build();

        project = Project.builder()
                .name("GRUAPIM")
                .createdBy(user)
                .build();

        gitRepo = GitRepository.builder()
                .project(project)
                .repositoryUrl("https://github.com/RecheEduardo/GRUAPIM-grupo-6")
                .provider("GitHub")
                .connectedBy(user)
                .build();

        task = Task.builder()
                .title("Implementar tela de login")
                .story(null)
                .createdBy(user)
                .build();
    }

    @Test
    void deveRegistrarRepositorioComSucesso() {
        UUID projectId = UUID.randomUUID();
        RegisterGitRepoRequest request = new RegisterGitRepoRequest(
                projectId, "https://github.com/RecheEduardo/GRUAPIM-grupo-6", "GitHub", null
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(gitRepoRepository.existsByProjectIdAndRepositoryUrl(projectId, request.repositoryUrl())).thenReturn(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gitRepoRepository.save(any(GitRepository.class))).thenReturn(gitRepo);

        GitRepoResponse response = gitService.registerRepository(request, user.getEmail());

        assertThat(response.repositoryUrl()).isEqualTo("https://github.com/RecheEduardo/GRUAPIM-grupo-6");
        assertThat(response.provider()).isEqualTo("GitHub");
    }

    @Test
    void deveLancarExcecaoQuandoRepositorioJaVinculado() {
        UUID projectId = UUID.randomUUID();
        RegisterGitRepoRequest request = new RegisterGitRepoRequest(
                projectId, "https://github.com/RecheEduardo/GRUAPIM-grupo-6", "GitHub", null
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(gitRepoRepository.existsByProjectIdAndRepositoryUrl(projectId, request.repositoryUrl())).thenReturn(true);

        assertThatThrownBy(() -> gitService.registerRepository(request, user.getEmail()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Repositório já vinculado a este projeto");
    }

    @Test
    void deveVincularCommitAUmaTarefa() {
        UUID taskId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();
        LinkCommitRequest request = new LinkCommitRequest(
                taskId, repoId, "abc123def456", "feat: implement login", "main", Instant.now()
        );

        GitCommitLink link = GitCommitLink.builder()
                .task(task)
                .repository(gitRepo)
                .commitHash("abc123def456")
                .commitMessage("feat: implement login")
                .branchName("main")
                .committedAt(request.committedAt())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(gitRepoRepository.findById(repoId)).thenReturn(Optional.of(gitRepo));
        when(gitCommitLinkRepository.existsByTaskIdAndCommitHash(taskId, "abc123def456")).thenReturn(false);
        when(gitCommitLinkRepository.save(any(GitCommitLink.class))).thenReturn(link);

        GitCommitLinkResponse response = gitService.linkCommit(request);

        assertThat(response.commitHash()).isEqualTo("abc123def456");
        assertThat(response.branchName()).isEqualTo("main");
    }

    @Test
    void deveLancarExcecaoQuandoCommitJaVinculado() {
        UUID taskId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();
        LinkCommitRequest request = new LinkCommitRequest(
                taskId, repoId, "abc123def456", null, null, Instant.now()
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(gitRepoRepository.findById(repoId)).thenReturn(Optional.of(gitRepo));
        when(gitCommitLinkRepository.existsByTaskIdAndCommitHash(taskId, "abc123def456")).thenReturn(true);

        assertThatThrownBy(() -> gitService.linkCommit(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Commit já vinculado a esta tarefa");
    }
}
