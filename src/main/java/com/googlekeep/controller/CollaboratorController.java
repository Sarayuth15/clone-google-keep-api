package com.googlekeep.controller;

import com.googlekeep.dto.request.CollaboratorRequest;
import com.googlekeep.dto.response.ApiResponse;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.service.CollaboratorService;
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
@RequestMapping("/api/notes/{noteId}/collaborators")
@RequiredArgsConstructor
@Tag(name = "Collaborators", description = "Share notes and manage collaborators")
@SecurityRequirement(name = "bearerAuth")
public class CollaboratorController {

    private final CollaboratorService collaboratorService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Add a collaborator to a note by email")
    public ResponseEntity<ApiResponse<NoteResponse>> addCollaborator(
        @PathVariable Long noteId,
        @Valid @RequestBody CollaboratorRequest.Add request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                collaboratorService.addCollaborator(noteId, request, securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{collaboratorUserId}/permission")
    @Operation(summary = "Update a collaborator's permission (VIEW or EDIT)")
    public ResponseEntity<ApiResponse<NoteResponse>> updatePermission(
        @PathVariable Long noteId,
        @PathVariable Long collaboratorUserId,
        @Valid @RequestBody CollaboratorRequest.UpdatePermission request) {
        return ResponseEntity.ok(ApiResponse.success(
            collaboratorService.updateCollaboratorPermission(noteId, collaboratorUserId, request, securityUtils.getCurrentUserId())));
    }

    @DeleteMapping("/{collaboratorUserId}")
    @Operation(summary = "Remove a collaborator from a note")
    public ResponseEntity<ApiResponse<NoteResponse>> removeCollaborator(
        @PathVariable Long noteId,
        @PathVariable Long collaboratorUserId) {
        return ResponseEntity.ok(ApiResponse.success(
            collaboratorService.removeCollaborator(noteId, collaboratorUserId, securityUtils.getCurrentUserId())));
    }

    @DeleteMapping("/leave")
    @Operation(summary = "Leave a note you are collaborating on")
    public ResponseEntity<ApiResponse<Void>> leaveNote(@PathVariable Long noteId) {
        collaboratorService.leaveNote(noteId, securityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Left the note"));
    }
}
