package com.gruapim.controller;

import com.gruapim.dto.request.CreateMeetingNoteRequest;
import com.gruapim.dto.response.MeetingNoteResponse;
import com.gruapim.service.MeetingNoteService;
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
@RequestMapping("/meeting-notes")
@RequiredArgsConstructor
public class MeetingNoteController {

    private final MeetingNoteService meetingNoteService;

    @PostMapping
    public ResponseEntity<MeetingNoteResponse> create(
            @Valid @RequestBody CreateMeetingNoteRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingNoteService.create(request, principal.getUsername()));
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<MeetingNoteResponse>> listBySprint(@PathVariable UUID sprintId) {
        return ResponseEntity.ok(meetingNoteService.listBySprint(sprintId));
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