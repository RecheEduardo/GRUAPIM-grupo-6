package com.gruapim.controller;

import com.gruapim.dto.response.BurndownResponse;
import com.gruapim.service.BurndownService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/burndown")
@RequiredArgsConstructor
public class BurndownController {

    private final BurndownService burndownService;

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<BurndownResponse> getBurndown(@PathVariable UUID sprintId) {
        return ResponseEntity.ok(burndownService.getBurndown(sprintId));
    }
}