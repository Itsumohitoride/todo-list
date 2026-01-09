package com.todo.todoList.infrastructure.adapter.in.rest;

import com.todo.todoList.application.dto.SharingDTO;
import com.todo.todoList.domain.port.in.IManageSharingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Sharing operations - Infrastructure Layer (Input Adapter)
 */
@RestController
@RequestMapping("/api/sharing")
@Tag(name = "Sharing", description = "TodoList sharing endpoints (QR codes and shareable links)")
public class SharingController {

    private final IManageSharingUseCase sharingUseCase;

    public SharingController(IManageSharingUseCase sharingUseCase) {
        this.sharingUseCase = sharingUseCase;
    }

    @Operation(summary = "Create sharing for a TodoList",
               description = "Creates a sharing entry with a unique token and shareable link for a TodoList. The sharing token can be used to generate QR codes or share via link.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sharing created successfully",
                    content = @Content(schema = @Schema(implementation = SharingDTO.class))),
            @ApiResponse(responseCode = "404", description = "TodoList not found"),
            @ApiResponse(responseCode = "409", description = "Sharing already exists for this TodoList")
    })
    @PostMapping("/lists/{listId}")
    public ResponseEntity<SharingDTO> createSharing(@PathVariable UUID listId) {
        SharingDTO sharing = sharingUseCase.createSharing(listId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sharing);
    }

    @Operation(summary = "Get sharing by ID",
               description = "Retrieves sharing information including token, link, and shared users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sharing found",
                    content = @Content(schema = @Schema(implementation = SharingDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sharing not found")
    })
    @GetMapping("/{sharingId}")
    public ResponseEntity<SharingDTO> getSharingById(@PathVariable UUID sharingId) {
        SharingDTO sharing = sharingUseCase.getSharingById(sharingId);
        return ResponseEntity.ok(sharing);
    }

    @Operation(summary = "Get sharing by share token",
               description = "Retrieves sharing information using the unique share token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sharing found",
                    content = @Content(schema = @Schema(implementation = SharingDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sharing not found")
    })
    @GetMapping("/token/{shareToken}")
    public ResponseEntity<SharingDTO> getSharingByToken(@PathVariable String shareToken) {
        SharingDTO sharing = sharingUseCase.getSharingByToken(shareToken);
        return ResponseEntity.ok(sharing);
    }

    @Operation(summary = "Get sharing for a TodoList",
               description = "Retrieves sharing information for a specific TodoList.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sharing found",
                    content = @Content(schema = @Schema(implementation = SharingDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sharing not found for this TodoList")
    })
    @GetMapping("/lists/{listId}")
    public ResponseEntity<SharingDTO> getSharingByTodoList(@PathVariable UUID listId) {
        SharingDTO sharing = sharingUseCase.getSharingByTodoListId(listId);
        return ResponseEntity.ok(sharing);
    }

    @Operation(summary = "Join shared list",
               description = "Allows a user to join a shared TodoList using the share token. The user will gain access to view and edit the list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User joined successfully",
                    content = @Content(schema = @Schema(implementation = SharingDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sharing or User not found"),
            @ApiResponse(responseCode = "409", description = "User already has access to this list")
    })
    @PostMapping("/join/{shareToken}")
    public ResponseEntity<SharingDTO> joinSharedList(
            @PathVariable String shareToken,
            @RequestBody JoinRequest joinRequest) {
        SharingDTO sharing = sharingUseCase.joinSharedList(shareToken, joinRequest.getUserId());
        return ResponseEntity.ok(sharing);
    }

    @Operation(summary = "Get shared users",
               description = "Retrieves all users who have access to a shared TodoList.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "TodoList or Sharing not found")
    })
    @GetMapping("/lists/{listId}/users")
    public ResponseEntity<List<UUID>> getSharedUsers(@PathVariable UUID listId) {
        List<UUID> userIds = sharingUseCase.getSharedUsers(listId);
        return ResponseEntity.ok(userIds);
    }

    @Operation(summary = "Generate QR code",
               description = "Generates a QR code image (PNG) for sharing a TodoList. The QR code contains the shareable link that users can scan to join the list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR code generated successfully",
                    content = @Content(mediaType = "image/png")),
            @ApiResponse(responseCode = "404", description = "Sharing not found"),
            @ApiResponse(responseCode = "400", description = "Invalid QR code dimensions")
    })
    @GetMapping("/qr/{shareToken}")
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable String shareToken,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {

        // Validate dimensions
        if (width < 100 || width > 1000 || height < 100 || height > 1000) {
            return ResponseEntity.badRequest().build();
        }

        byte[] qrCode = sharingUseCase.generateQRCode(shareToken, width, height);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCode.length);
        headers.set("Content-Disposition", "inline; filename=qrcode.png");

        return ResponseEntity.ok()
                .headers(headers)
                .body(qrCode);
    }

    @Operation(summary = "Delete sharing",
               description = "Deletes a sharing and revokes access for all shared users. This action cannot be undone.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sharing deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sharing not found")
    })
    @DeleteMapping("/{sharingId}")
    public ResponseEntity<Void> deleteSharing(@PathVariable UUID sharingId) {
        sharingUseCase.deleteSharing(sharingId);
        return ResponseEntity.noContent().build();
    }

    // ====================================
    // Inner Classes for Request Bodies
    // ====================================

    @Getter
    @Setter
    @Schema(description = "Request body for joining a shared list")
    public static class JoinRequest {
        @Schema(description = "User ID joining the shared list", required = true)
        private UUID userId;
    }
}
