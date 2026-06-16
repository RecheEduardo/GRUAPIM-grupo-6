package com.gruapim.service;

import com.gruapim.domain.entity.Sprint;
import com.gruapim.domain.entity.SprintStory;
import com.gruapim.dto.response.BurndownPointResponse;
import com.gruapim.dto.response.BurndownResponse;
import com.gruapim.repository.SprintRepository;
import com.gruapim.repository.SprintStoryRepository;
import com.gruapim.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BurndownService {

    private final SprintRepository sprintRepository;
    private final SprintStoryRepository sprintStoryRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public BurndownResponse getBurndown(UUID sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new IllegalArgumentException("Sprint não encontrado"));

        List<SprintStory> sprintStories = sprintStoryRepository.findBySprintId(sprintId);

        int totalPoints = sprintStories.stream()
                .mapToInt(ss -> ss.getStory().getStoryPoints() != null ? ss.getStory().getStoryPoints() : 0)
                .sum();

        List<BurndownPointResponse> data = buildBurndownData(sprint, sprintStories, totalPoints);

        return new BurndownResponse(sprintId, sprint.getName(), totalPoints, data);
    }

    private List<BurndownPointResponse> buildBurndownData(Sprint sprint,
                                                          List<SprintStory> stories,
                                                          int totalPoints) {
        List<BurndownPointResponse> points = new ArrayList<>();
        LocalDate start = sprint.getStartDate();
        LocalDate end = sprint.getEndDate();
        long totalDays = start.until(end).getDays();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            int completedPoints = stories.stream()
                    .filter(ss -> isStoryCompletedByDate(ss, currentDate))
                    .mapToInt(ss -> ss.getStory().getStoryPoints() != null ? ss.getStory().getStoryPoints() : 0)
                    .sum();

            long dayIndex = start.until(currentDate).getDays();
            int idealRemaining = totalDays > 0
                    ? (int) Math.round(totalPoints * (1.0 - (double) dayIndex / totalDays))
                    : 0;

            points.add(new BurndownPointResponse(
                    currentDate,
                    totalPoints - completedPoints,
                    idealRemaining
            ));
        }

        return points;
    }

    private boolean isStoryCompletedByDate(SprintStory ss, LocalDate date) {
        long doneTasks = taskRepository.countDoneTasksByStoryIdAndDate(
                ss.getStory().getId(), date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        long totalTasks = taskRepository.countByStoryId(ss.getStory().getId());
        return totalTasks > 0 && doneTasks >= totalTasks;
    }
}