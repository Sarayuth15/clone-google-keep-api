package com.googlekeep.controller;

import com.googlekeep.dto.request.ChecklistItemRequest;
import com.googlekeep.dto.response.ApiResponse;
import com.googlekeep.dto.response.NoteResponse;
import com.googlekeep.service.ChecklistItemService;
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
@RequestMapping("/api/notes/{noteId}/checklist")
@RequiredArgsConstructor
@Tag(name = "Checklist Items", description = "Manage checklist items within a note")
@SecurityRequirement(name = "bearerAuth")
public class ChecklistItemController {

    private final ChecklistItemService checklistItemService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Add a checklist item to a note")
    public ResponseEntity<ApiResponse<NoteResponse>> addItem(
        @PathVariable Long noteId,
        @Valid @RequestBody ChecklistItemRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(checklistItemService.addItem(noteId, request, securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Update a checklist item (text, checked state, position)")
    public ResponseEntity<ApiResponse<NoteResponse>> updateItem(
        @PathVariable Long noteId,
        @PathVariable Long itemId,
        @Valid @RequestBody ChecklistItemRequest.Update request) {
        return ResponseEntity.ok(ApiResponse.success(
            checklistItemService.updateItem(noteId, itemId, request, securityUtils.getCurrentUserId())));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Delete a checklist item")
    public ResponseEntity<ApiResponse<NoteResponse>> deleteItem(
        @PathVariable Long noteId,
        @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(
            checklistItemService.deleteItem(noteId, itemId, securityUtils.getCurrentUserId())));
    }

    @DeleteMapping("/checked")
    @Operation(summary = "Clear all checked items from checklist")
    public ResponseEntity<ApiResponse<NoteResponse>> clearChecked(@PathVariable Long noteId) {
        return ResponseEntity.ok(ApiResponse.success(
            checklistItemService.clearChecked(noteId, securityUtils.getCurrentUserId())));
    }
}
