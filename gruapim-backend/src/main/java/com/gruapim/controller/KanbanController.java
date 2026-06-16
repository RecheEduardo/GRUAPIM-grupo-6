package com.gruapim.controller;

import com.gruapim.dto.request.CreateKanbanColumnRequest;
import com.gruapim.dto.response.KanbanColumnResponse;
import com.gruapim.service.KanbanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kanban")
@RequiredArgsConstructor
public class KanbanController {

    private final KanbanService kanbanService;

    @PostMapping("/columns")
    public ResponseEntity<KanbanColumnResponse> createColumn(
            @Valid @RequestBody CreateKanbanColumnRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(kanbanService.createColumn(request));
    }

    @GetMapping("/columns/project/{projectId}")
    public ResponseEntity<List<KanbanColumnResponse>> listByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(kanbanService.listByProject(projectId));
    }

    @DeleteMapping("/columns/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable UUID columnId) {
        kanbanService.deleteColumn(columnId);
        return ResponseEntity.noContent().build();
    }
}