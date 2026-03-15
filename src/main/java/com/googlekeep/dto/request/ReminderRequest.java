package com.googlekeep.dto.request;

import com.googlekeep.entity.enums.ReminderRepeat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public class ReminderRequest {

    @Data
    public static class Create {
        @NotNull(message = "Reminder time is required")
        @Future(message = "Reminder must be in the future")
        private LocalDateTime remindAt;
        private ReminderRepeat repeat = ReminderRepeat.NONE;
    }

    @Data
    public static class Update {
        @Future
        private LocalDateTime remindAt;
        private ReminderRepeat repeat;
    }
}
