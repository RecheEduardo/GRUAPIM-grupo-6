package com.gruapim.service;

import com.gruapim.domain.entity.GitCommitLink;
import com.gruapim.domain.entity.GitRepository;
import com.gruapim.domain.entity.Task;
import com.gruapim.domain.entity.User;
import com.gruapim.dto.request.LinkCommitRequest;
import com.gruapim.dto.request.RegisterGitRepoRequest;
import com.gruapim.dto.response.GitCommitLinkResponse;
import com.gruapim.dto.response.GitRepoResponse;
import com.gruapim.repository.GitCommitLinkRepository;
import com.gruapim.repository.GitRepoRepository;
import com.gruapim.repository.ProjectRepository;
import com.gruapim.repository.TaskRepository;
import com.gruapim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GitService {

    private final GitRepoRepository gitRepoRepository;
    private final GitCommitLinkRepository gitCommitLinkRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public GitRepoResponse registerRepository(RegisterGitRepoRequest request, String connectorEmail) {
        var project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));

        if (gitRepoRepository.existsByProjectIdAndRepositoryUrl(request.projectId(), request.repositoryUrl())) {
            throw new IllegalStateException("Repositório já vinculado a este projeto");
        }

        User connector = userRepository.findByEmail(connectorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        GitRepository repo = GitRepository.builder()
                .project(project)
                .repositoryUrl(request.repositoryUrl())
                .provider(request.provider())
                .accessTokenEncrypted(request.accessToken())
                .connectedBy(connector)
                .build();

        return GitRepoResponse.from(gitRepoRepository.save(repo));
    }

    @Transactional(readOnly = true)
    public List<GitRepoResponse> listByProject(UUID projectId) {
        return gitRepoRepository.findByProjectId(projectId)
                .stream().map(GitRepoResponse::from).toList();
    }

    @Transactional
    public void removeRepository(UUID repoId) {
        GitRepository repo = gitRepoRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("Repositório não encontrado"));
        gitRepoRepository.delete(repo);
    }

    @Transactional
    public GitCommitLinkResponse linkCommit(LinkCommitRequest request) {
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));

        GitRepository repo = gitRepoRepository.findById(request.repositoryId())
                .orElseThrow(() -> new IllegalArgumentException("Repositório não encontrado"));

        if (gitCommitLinkRepository.existsByTaskIdAndCommitHash(request.taskId(), request.commitHash())) {
            throw new IllegalStateException("Commit já vinculado a esta tarefa");
        }

        GitCommitLink link = GitCommitLink.builder()
                .task(task)
                .repository(repo)
                .commitHash(request.commitHash())
                .commitMessage(request.commitMessage())
                .branchName(request.branchName())
                .committedAt(request.committedAt())
                .build();

        return GitCommitLinkResponse.from(gitCommitLinkRepository.save(link));
    }

    @Transactional(readOnly = true)
    public List<GitCommitLinkResponse> listCommitsByTask(UUID taskId) {
        return gitCommitLinkRepository.findByTaskId(taskId)
                .stream().map(GitCommitLinkResponse::from).toList();
    }

    @Transactional
    public void unlinkCommit(UUID commitLinkId) {
        GitCommitLink link = gitCommitLinkRepository.findById(commitLinkId)
                .orElseThrow(() -> new IllegalArgumentException("Vínculo de commit não encontrado"));
        gitCommitLinkRepository.delete(link);
    }
}
