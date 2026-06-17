package com.gruapim.controller;

import com.gruapim.dto.request.CreateUserStoryRequest;
import com.gruapim.dto.request.UpdateUserStoryRequest;
import com.gruapim.dto.response.UserStoryResponse;
import com.gruapim.service.UserStoryService;
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
@RequestMapping("/user-stories")
@RequiredArgsConstructor
public class UserStoryController {

    private final UserStoryService userStoryService;

    @PostMapping
    public ResponseEntity<UserStoryResponse> create(
            @Valid @RequestBody CreateUserStoryRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userStoryService.create(request, principal.getUsername()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<UserStoryResponse>> listByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(userStoryService.listByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userStoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserStoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStoryRequest request) {
        return ResponseEntity.ok(userStoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userStoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}