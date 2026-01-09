package com.todo.todoList.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Sharing Data Transfer Object")
public class SharingDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Sharing unique identifier (auto-generated)", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Unique token for sharing (auto-generated)", accessMode = Schema.AccessMode.READ_ONLY, example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String shareToken;

    @Schema(description = "ID of the TodoList being shared", required = true)
    private UUID todoListId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "When the sharing was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "List of user IDs that have access to this shared list", accessMode = Schema.AccessMode.READ_ONLY)
    private List<UUID> sharedUserIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Shareable link URL", accessMode = Schema.AccessMode.READ_ONLY, example = "http://localhost:8080/api/sharing/join/a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String shareableLink;
}
