package com.gruapim.collaboration.controller;

import com.gruapim.collaboration.dto.request.CreateMeetingNoteRequest;
import com.gruapim.collaboration.dto.response.MeetingNoteResponse;
import com.gruapim.collaboration.security.UserPrincipal;
import com.gruapim.collaboration.service.MeetingNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meeting-notes")
@RequiredArgsConstructor
public class MeetingNoteController {

    private final MeetingNoteService meetingNoteService;

    @PostMapping
    public ResponseEntity<MeetingNoteResponse> create(
            @Valid @RequestBody CreateMeetingNoteRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingNoteService.create(request, principal));
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<MeetingNoteResponse>> listBySprint(@PathVariable UUID sprintId) {
        return ResponseEntity.ok(meetingNoteService.listBySprint(sprintId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<MeetingNoteResponse>> listByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(meetingNoteService.listByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingNoteResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(meetingNoteService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        meetingNoteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
