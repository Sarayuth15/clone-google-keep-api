package com.googlekeep.controller;

import com.googlekeep.dto.request.NoteRequest;
import com.googlekeep.dto.response.ApiResponse;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.entity.enums.NoteColor;
import com.googlekeep.service.NoteService;
import com.googlekeep.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "CRUD and state management for notes")
@SecurityRequirement(name = "bearerAuth")
public class NoteController {

    private final NoteService noteService;
    private final SecurityUtils securityUtils;

    // ── GET ───────────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all active notes (not archived, not trashed)")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getActiveNotes() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(noteService.getActiveNotes(userId)));
    }

    @GetMapping("/archived")
    @Operation(summary = "Get archived notes")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getArchivedNotes() {
        return ResponseEntity.ok(ApiResponse.success(noteService.getArchivedNotes(securityUtils.getCurrentUserId())));
    }

    @GetMapping("/trash")
    @Operation(summary = "Get trashed notes")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getTrashedNotes() {
        return ResponseEntity.ok(ApiResponse.success(noteService.getTrashedNotes(securityUtils.getCurrentUserId())));
    }

    @GetMapping("/shared")
    @Operation(summary = "Get notes shared with current user")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getSharedNotes() {
        return ResponseEntity.ok(ApiResponse.success(noteService.getSharedNotes(securityUtils.getCurrentUserId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific note by ID")
    public ResponseEntity<ApiResponse<NoteResponse>> getNote(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(noteService.getNoteById(id, securityUtils.getCurrentUserId())));
    }

    @GetMapping("/search")
    @Operation(summary = "Search notes by title or content")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> searchNotes(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(noteService.searchNotes(q, securityUtils.getCurrentUserId())));
    }

    @GetMapping("/by-label/{labelId}")
    @Operation(summary = "Get notes filtered by label")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getNotesByLabel(@PathVariable Long labelId) {
        return ResponseEntity.ok(ApiResponse.success(noteService.getNotesByLabel(labelId, securityUtils.getCurrentUserId())));
    }

    @GetMapping("/by-color/{color}")
    @Operation(summary = "Get notes filtered by color")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getNotesByColor(@PathVariable NoteColor color) {
        return ResponseEntity.ok(ApiResponse.success(noteService.getNotesByColor(color, securityUtils.getCurrentUserId())));
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new note")
    public ResponseEntity<ApiResponse<NoteResponse>> createNote(@Valid @RequestBody NoteRequest.Create request) {
        NoteResponse note = noteService.createNote(request, securityUtils.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Note created", note));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update note title, content, color, labels")
    public ResponseEntity<ApiResponse<NoteResponse>> updateNote(
        @PathVariable Long id,
        @Valid @RequestBody NoteRequest.Update request) {
        return ResponseEntity.ok(ApiResponse.success(noteService.updateNote(id, request, securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{id}/color")
    @Operation(summary = "Change note color")
    public ResponseEntity<ApiResponse<NoteResponse>> updateColor(
        @PathVariable Long id,
        @Valid @RequestBody NoteRequest.UpdateColor request) {
        return ResponseEntity.ok(ApiResponse.success(
            noteService.updateColor(id, request.getColor(), securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{id}/pin")
    @Operation(summary = "Toggle pin/unpin a note")
    public ResponseEntity<ApiResponse<NoteResponse>> togglePin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(noteService.togglePin(id, securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Toggle archive/unarchive a note")
    public ResponseEntity<ApiResponse<NoteResponse>> toggleArchive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(noteService.toggleArchive(id, securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{id}/trash")
    @Operation(summary = "Move note to trash")
    public ResponseEntity<ApiResponse<NoteResponse>> trashNote(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(noteService.trashNote(id, securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restore note from trash")
    public ResponseEntity<ApiResponse<NoteResponse>> restoreNote(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(noteService.restoreNote(id, securityUtils.getCurrentUserId())));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently delete a trashed note")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        noteService.deleteNotePermanently(id, securityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Note permanently deleted"));
    }

    @DeleteMapping("/trash")
    @Operation(summary = "Empty trash - permanently delete all trashed notes")
    public ResponseEntity<ApiResponse<Void>> emptyTrash() {
        noteService.emptyTrash(securityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Trash emptied"));
    }
}
