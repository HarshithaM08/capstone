package com.savingsapp.groupservice.controller;

import com.savingsapp.groupservice.dto.*;
import com.savingsapp.groupservice.security.JwtUserDetails;
import com.savingsapp.groupservice.service.SavingsGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Slf4j
public class SavingsGroupController {

    private final SavingsGroupService savingsGroupService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request,
                                                                  @AuthenticationPrincipal JwtUserDetails userDetails) {
        log.info("Creating new savings group for user: {}", userDetails.getUserId());
        GroupResponse group = savingsGroupService.createGroup(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Group created successfully", group));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroups() {
        log.info("Fetching all savings groups");
        List<GroupResponse> groups = savingsGroupService.getAllGroups();
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable String groupId) {
        log.info("Fetching savings group with id: {}", groupId);
        GroupResponse group = savingsGroupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success(group));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(@PathVariable String groupId,
                                                                  @Valid @RequestBody UpdateGroupRequest request,
                                                                  @AuthenticationPrincipal JwtUserDetails userDetails) {
        log.info("Updating savings group with id: {}", groupId);
        GroupResponse group = savingsGroupService.updateGroup(groupId, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully", group));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable String groupId,
                                                         @AuthenticationPrincipal JwtUserDetails userDetails) {
        log.info("Deleting savings group with id: {}", groupId);
        savingsGroupService.deleteGroup(groupId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<GroupResponse>> joinGroup(@PathVariable String groupId,
                                                                @RequestParam(required = false) String userName,
                                                                @AuthenticationPrincipal JwtUserDetails userDetails) {
        log.info("User {} requesting to join group: {}", userDetails.getUserId(), groupId);
        GroupResponse group = savingsGroupService.joinGroup(groupId, userDetails.getUserId(), userName);
        return ResponseEntity.ok(ApiResponse.success("Join request submitted successfully", group));
    }

    @PostMapping("/{groupId}/users/{userId}/respond")
    public ResponseEntity<ApiResponse<GroupResponse>> respondToJoinRequest(@PathVariable String groupId,
                                                                           @PathVariable String userId,
                                                                           @Valid @RequestBody MemberManagementRequest request,
                                                                           @AuthenticationPrincipal JwtUserDetails userDetails) {
        log.info("Responding to join request for user {} in group {}", userId, groupId);
        GroupResponse group = savingsGroupService.respondToJoinRequest(groupId, userId, request.getApproved(), userDetails.getUserId());
        String message = request.getApproved() ? "User approved successfully" : "User rejected successfully";
        return ResponseEntity.ok(ApiResponse.success(message, group));
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getOrganizerGroups(@PathVariable String organizerId) {
        log.info("Fetching groups for organizer: {}", organizerId);
        List<GroupResponse> groups = savingsGroupService.getOrganizerGroups(organizerId);
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    @PostMapping("/{groupId}/assign-next")
    public ResponseEntity<ApiResponse<GroupResponse>> assignNextRecipient(@PathVariable String groupId,
                                                                          @AuthenticationPrincipal JwtUserDetails userDetails) {
        log.info("Assigning next recipient for group: {}", groupId);
        GroupResponse group = savingsGroupService.assignNextRecipient(groupId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Next recipient assigned successfully", group));
    }

    @PostMapping("/{groupId}/close")
    public ResponseEntity<ApiResponse<GroupResponse>> closeGroup(@PathVariable String groupId,
                                                                 @AuthenticationPrincipal JwtUserDetails userDetails) {
        log.info("Closing group: {}", groupId);
        GroupResponse group = savingsGroupService.closeGroup(groupId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Group closed successfully", group));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Group Service is healthy");
    }
}