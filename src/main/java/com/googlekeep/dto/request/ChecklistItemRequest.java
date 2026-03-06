package com.googlekeep.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ChecklistItemRequest {

    @Data
    public static class Create {
        @NotBlank(message = "Text is required")
        @Size(max = 500)
        private String text;
        private boolean checked = false;
        private int position = 0;
    }

    @Data
    public static class Update {
        @Size(max = 500)
        private String text;
        private Boolean checked;
        private Integer position;
    }
}
