package com.gruapim.service;

import com.gruapim.domain.entity.MeetingNote;
import com.gruapim.domain.entity.Sprint;
import com.gruapim.domain.entity.User;
import com.gruapim.dto.request.CreateMeetingNoteRequest;
import com.gruapim.dto.response.MeetingNoteResponse;
import com.gruapim.repository.MeetingNoteRepository;
import com.gruapim.repository.SprintRepository;
import com.gruapim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingNoteService {

    private final MeetingNoteRepository meetingNoteRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;

    @Transactional
    public MeetingNoteResponse create(CreateMeetingNoteRequest request, String creatorEmail) {
        Sprint sprint = sprintRepository.findById(request.sprintId())
                .orElseThrow(() -> new IllegalArgumentException("Sprint não encontrado"));
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        MeetingNote note = MeetingNote.builder()
                .sprint(sprint)
                .type(request.type())
                .discussedPoints(request.discussedPoints())
                .decisions(request.decisions())
                .improvementActions(request.improvementActions())
                .createdBy(creator)
                .build();

        return MeetingNoteResponse.from(meetingNoteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<MeetingNoteResponse> listBySprint(UUID sprintId) {
        return meetingNoteRepository.findBySprintIdOrderByCreatedAtDesc(sprintId)
                .stream().map(MeetingNoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MeetingNoteResponse getById(UUID id) {
        return MeetingNoteResponse.from(meetingNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ata não encontrada")));
    }

    @Transactional
    public void delete(UUID id) {
        meetingNoteRepository.deleteById(id);
    }
}