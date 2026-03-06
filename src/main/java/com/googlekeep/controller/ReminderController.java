package com.googlekeep.controller;

import com.googlekeep.dto.request.ReminderRequest;
import com.googlekeep.dto.response.ApiResponse;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.service.ReminderService;
import com.googlekeep.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes/{noteId}/reminder")
@RequiredArgsConstructor
@Tag(name = "Reminders", description = "Set and manage reminders on notes")
@SecurityRequirement(name = "bearerAuth")
public class ReminderController {

    private final ReminderService reminderService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Set a reminder on a note")
    public ResponseEntity<ApiResponse<NoteResponse>> setReminder(
        @PathVariable Long noteId,
        @Valid @RequestBody ReminderRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(reminderService.setReminder(noteId, request, securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{reminderId}")
    @Operation(summary = "Update a reminder")
    public ResponseEntity<ApiResponse<NoteResponse>> updateReminder(
        @PathVariable Long noteId,
        @PathVariable Long reminderId,
        @Valid @RequestBody ReminderRequest.Update request) {
        return ResponseEntity.ok(ApiResponse.success(
            reminderService.updateReminder(noteId, reminderId, request, securityUtils.getCurrentUserId())));
    }

    @DeleteMapping
    @Operation(summary = "Remove reminder from a note")
    public ResponseEntity<ApiResponse<NoteResponse>> deleteReminder(@PathVariable Long noteId) {
        return ResponseEntity.ok(ApiResponse.success(
            reminderService.deleteReminder(noteId, securityUtils.getCurrentUserId())));
    }
}
