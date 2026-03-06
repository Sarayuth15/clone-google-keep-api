package com.googlekeep.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LabelResponse {
    private Long id;
    private String name;
    private int noteCount;
    private LocalDateTime createdAt;
}
