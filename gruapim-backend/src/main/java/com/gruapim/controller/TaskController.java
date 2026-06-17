package com.gruapim.controller;

import com.gruapim.dto.request.CreateTaskRequest;
import com.gruapim.dto.request.MoveTaskRequest;
import com.gruapim.dto.request.UpdateTaskRequest;
import com.gruapim.dto.response.TaskResponse;
import com.gruapim.service.TaskService;
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
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(request, principal.getUsername()));
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<TaskResponse>> listByStory(@PathVariable UUID storyId) {
        return ResponseEntity.ok(taskService.listByStory(storyId));
    }

    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<TaskResponse>> listByColumn(@PathVariable UUID columnId) {
        return ResponseEntity.ok(taskService.listByColumn(columnId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(taskService.update(id, request, principal.getUsername()));
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<TaskResponse> move(
            @PathVariable UUID id,
            @Valid @RequestBody MoveTaskRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(taskService.move(id, request, principal.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}