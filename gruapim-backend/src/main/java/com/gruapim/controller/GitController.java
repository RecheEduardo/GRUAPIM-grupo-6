package com.gruapim.controller;

import com.gruapim.dto.request.LinkCommitRequest;
import com.gruapim.dto.request.RegisterGitRepoRequest;
import com.gruapim.dto.response.GitCommitLinkResponse;
import com.gruapim.dto.response.GitRepoResponse;
import com.gruapim.service.GitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/git")
@RequiredArgsConstructor
public class GitController {

    private final GitService gitService;

    @PostMapping("/repositories")
    public ResponseEntity<GitRepoResponse> registerRepository(
            @Valid @RequestBody RegisterGitRepoRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gitService.registerRepository(request, principal.getUsername()));
    }

    @GetMapping("/repositories/project/{projectId}")
    public ResponseEntity<List<GitRepoResponse>> listByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(gitService.listByProject(projectId));
    }

    @DeleteMapping("/repositories/{repoId}")
    public ResponseEntity<Void> removeRepository(@PathVariable UUID repoId) {
        gitService.removeRepository(repoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/commits")
    public ResponseEntity<GitCommitLinkResponse> linkCommit(
            @Valid @RequestBody LinkCommitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gitService.linkCommit(request));
    }

    @GetMapping("/commits/task/{taskId}")
    public ResponseEntity<List<GitCommitLinkResponse>> listCommitsByTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(gitService.listCommitsByTask(taskId));
    }

    @DeleteMapping("/commits/{commitLinkId}")
    public ResponseEntity<Void> unlinkCommit(@PathVariable UUID commitLinkId) {
        gitService.unlinkCommit(commitLinkId);
        return ResponseEntity.noContent().build();
    }
}
