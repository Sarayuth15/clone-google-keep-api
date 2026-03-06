package com.googlekeep.dto.response;

import com.googlekeep.entity.enums.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class NoteResponse {
    private Long id;
    private String title;
    private String content;
    private NoteType type;
    private NoteColor color;
    private boolean pinned;
    private boolean archived;
    private boolean trashed;
    private List<ChecklistItemResponse> checklistItems;
    private Set<LabelResponse> labels;
    private List<CollaboratorResponse> collaborators;
    private ReminderResponse reminder;
    private List<NoteImageResponse> images;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class ChecklistItemResponse {
        private Long id;
        private String text;
        private boolean checked;
        private int position;
    }

    @Data
    @Builder
    public static class LabelResponse {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class CollaboratorResponse {
        private Long userId;
        private String name;
        private String email;
        private CollaboratorPermission permission;
    }

    @Data
    @Builder
    public static class ReminderResponse {
        private Long id;
        private LocalDateTime remindAt;
        private ReminderRepeat repeat;
        private boolean fired;
    }

    @Data
    @Builder
    public static class NoteImageResponse {
        private Long id;
        private String imageUrl;
        private String altText;
        private int position;
    }
}
