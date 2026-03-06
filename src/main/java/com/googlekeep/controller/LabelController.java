package com.googlekeep.controller;

import com.googlekeep.dto.request.LabelRequest;
import com.googlekeep.dto.response.ApiResponse;
import com.googlekeep.dto.response.LabelResponse;
import com.googlekeep.service.LabelService;
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
@RequestMapping("/api/labels")
@RequiredArgsConstructor
@Tag(name = "Labels", description = "Manage note labels")
@SecurityRequirement(name = "bearerAuth")
public class LabelController {

    private final LabelService labelService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Get all labels for current user")
    public ResponseEntity<ApiResponse<List<LabelResponse>>> getLabels() {
        return ResponseEntity.ok(ApiResponse.success(labelService.getUserLabels(securityUtils.getCurrentUserId())));
    }

    @PostMapping
    @Operation(summary = "Create a new label")
    public ResponseEntity<ApiResponse<LabelResponse>> createLabel(@Valid @RequestBody LabelRequest.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Label created", labelService.createLabel(request, securityUtils.getCurrentUserId())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename a label")
    public ResponseEntity<ApiResponse<LabelResponse>> updateLabel(
        @PathVariable Long id,
        @Valid @RequestBody LabelRequest.Update request) {
        return ResponseEntity.ok(ApiResponse.success(labelService.updateLabel(id, request, securityUtils.getCurrentUserId())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a label")
    public ResponseEntity<ApiResponse<Void>> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id, securityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Label deleted"));
    }
}
