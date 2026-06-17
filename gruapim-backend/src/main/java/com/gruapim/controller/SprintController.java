package com.gruapim.controller;

import com.gruapim.dto.request.AddStoryToSprintRequest;
import com.gruapim.dto.request.CreateSprintRequest;
import com.gruapim.dto.request.UpdateSprintRequest;
import com.gruapim.dto.response.SprintResponse;
import com.gruapim.dto.response.UserStoryResponse;
import com.gruapim.service.SprintService;
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
@RequestMapping("/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    public ResponseEntity<SprintResponse> create(
            @Valid @RequestBody CreateSprintRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sprintService.create(request, principal.getUsername()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintResponse>> listByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(sprintService.listByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(sprintService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSprintRequest request) {
        return ResponseEntity.ok(sprintService.update(id, request));
    }

    @PostMapping("/{id}/stories")
    public ResponseEntity<Void> addStory(
            @PathVariable UUID id,
            @Valid @RequestBody AddStoryToSprintRequest request) {
        sprintService.addStory(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/stories")
    public ResponseEntity<List<UserStoryResponse>> listStories(@PathVariable UUID id) {
        return ResponseEntity.ok(sprintService.listStories(id));
    }

    @DeleteMapping("/{id}/stories/{storyId}")
    public ResponseEntity<Void> removeStory(@PathVariable UUID id, @PathVariable UUID storyId) {
        sprintService.removeStory(id, storyId);
        return ResponseEntity.noContent().build();
    }
}