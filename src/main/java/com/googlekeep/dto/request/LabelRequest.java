package com.googlekeep.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class LabelRequest {

    @Data
    public static class Create {
        @NotBlank(message = "Label name is required")
        @Size(max = 100, message = "Label name too long")
        private String name;
    }

    @Data
    public static class Update {
        @NotBlank(message = "Label name is required")
        @Size(max = 100)
        private String name;
    }
}
