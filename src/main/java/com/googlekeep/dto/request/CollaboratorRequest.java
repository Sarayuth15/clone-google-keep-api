package com.googlekeep.dto.request;

import com.googlekeep.entity.enums.CollaboratorPermission;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CollaboratorRequest {

    @Data
    public static class Add {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        private String email;

        @NotNull
        private CollaboratorPermission permission = CollaboratorPermission.EDIT;
    }

    @Data
    public static class UpdatePermission {
        @NotNull(message = "Permission is required")
        private CollaboratorPermission permission;
    }
}
