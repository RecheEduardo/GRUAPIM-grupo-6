package com.gruapim.collaboration.service;

import com.gruapim.collaboration.domain.entity.MeetingNote;
import com.gruapim.collaboration.dto.request.CreateMeetingNoteRequest;
import com.gruapim.collaboration.dto.response.MeetingNoteResponse;
import com.gruapim.collaboration.exception.ResourceNotFoundException;
import com.gruapim.collaboration.repository.MeetingNoteRepository;
import com.gruapim.collaboration.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingNoteService {

    private final MeetingNoteRepository meetingNoteRepository;

    @Transactional
    public MeetingNoteResponse create(CreateMeetingNoteRequest request, UserPrincipal creator) {
        MeetingNote note = MeetingNote.builder()
                .sprintId(request.sprintId())
                .projectId(request.projectId())
                .type(request.type())
                .discussedPoints(request.discussedPoints())
                .decisions(request.decisions())
                .improvementActions(request.improvementActions())
                .createdById(creator.id())
                .createdByName(creator.name())
                .build();

        return MeetingNoteResponse.from(meetingNoteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<MeetingNoteResponse> listBySprint(UUID sprintId) {
        return meetingNoteRepository.findBySprintIdOrderByCreatedAtDesc(sprintId)
                .stream().map(MeetingNoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MeetingNoteResponse> listByProject(UUID projectId) {
        return meetingNoteRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream().map(MeetingNoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MeetingNoteResponse getById(UUID id) {
        return MeetingNoteResponse.from(meetingNoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ata não encontrada")));
    }

    @Transactional
    public void delete(UUID id) {
        meetingNoteRepository.deleteById(id);
    }
}
