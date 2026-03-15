package com.googlekeep.dto.request;

import com.googlekeep.entity.enums.NoteColor;
import com.googlekeep.entity.enums.NoteType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoteRequest {

    @Data
    public static class Create {
        @Size(max = 1000, message = "Title too long")
        private String title;
        private String content;

        @NotNull
        private NoteType type = NoteType.TEXT;

        private NoteColor color = NoteColor.DEFAULT;

        private boolean pinned = false;

        @Valid
        private List<ChecklistItemRequest.Create> checklistItems = new ArrayList<>();

        private Set<Long> labelIds = new HashSet<>();

        private List<String> imageUrls = new ArrayList<>();
    }

    @Data
    public static class Update {
        @Size(max = 1000)
        private String title;

        private String content;

        private NoteColor color;

        private Boolean pinned;

        private Set<Long> labelIds;
    }

    @Data
    public static class UpdateColor {
        @NotNull(message = "Color is required")
        private NoteColor color;
    }
}
