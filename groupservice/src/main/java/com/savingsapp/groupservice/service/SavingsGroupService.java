package com.savingsapp.groupservice.service;

import com.savingsapp.groupservice.dto.CreateGroupRequest;
import com.savingsapp.groupservice.dto.GroupResponse;
import com.savingsapp.groupservice.dto.UpdateGroupRequest;
import com.savingsapp.groupservice.exception.ResourceNotFoundException;
import com.savingsapp.groupservice.exception.UnauthorizedException;
import com.savingsapp.groupservice.model.GroupMember;
import com.savingsapp.groupservice.model.SavingsGroup;
import com.savingsapp.groupservice.repository.SavingsGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsGroupService {

    private final SavingsGroupRepository savingsGroupRepository;

    public GroupResponse createGroup(CreateGroupRequest request, String organizerId) {
        SavingsGroup group = SavingsGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .organizerId(organizerId)
                .contributionAmount(request.getContributionAmount())
                .currency(request.getCurrency())
                .cycleDurationInMonths(request.getCycleDurationInMonths())
                .maxMembers(request.getMaxMembers())
                .createdAt(LocalDateTime.now())
                .startDate(request.getStartDate())
                .status(SavingsGroup.GroupStatus.OPEN)
                .currentCycle(0)
                .totalCycles(request.getMaxMembers())
                .build();

        // Add organizer as the first member
        GroupMember organizer = GroupMember.builder()
                .userId(organizerId)
                .joinedAt(LocalDateTime.now())
                .status(GroupMember.MemberStatus.ACTIVE)
                .build();

        group.getMembers().add(organizer);

        SavingsGroup savedGroup = savingsGroupRepository.save(group);
        return GroupResponse.fromEntity(savedGroup);
    }

    public List<GroupResponse> getAllGroups() {
        return savingsGroupRepository.findAll().stream()
                .map(GroupResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public GroupResponse getGroupById(String groupId) {
        SavingsGroup group = savingsGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        return GroupResponse.fromEntity(group);
    }

    public GroupResponse updateGroup(String groupId, UpdateGroupRequest request, String organizerId) {
        SavingsGroup group = savingsGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        // Check if user is the organizer
        if (!group.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedException("Only the organizer can update this group");
        }

        // Don't allow updates if group is active or completed
        if (group.getStatus() == SavingsGroup.GroupStatus.ACTIVE ||
                group.getStatus() == SavingsGroup.GroupStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update an active or completed group");
        }

        if (request.getName() != null) {
            group.setName(request.getName());
        }

        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }

        if (request.getContributionAmount() != null) {
            group.setContributionAmount(request.getContributionAmount());
        }

        if (request.getCurrency() != null) {
            group.setCurrency(request.getCurrency());
        }

        if (request.getCycleDurationInMonths() != null) {
            group.setCycleDurationInMonths(request.getCycleDurationInMonths());
        }

        if (request.getStartDate() != null) {
            group.setStartDate(request.getStartDate());
        }

        SavingsGroup updatedGroup = savingsGroupRepository.save(group);
        return GroupResponse.fromEntity(updatedGroup);
    }

    public void deleteGroup(String groupId, String organizerId) {
        SavingsGroup group = savingsGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        // Check if user is the organizer
        if (!group.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedException("Only the organizer can delete this group");
        }

        // Don't allow deletion if group is active
        if (group.getStatus() == SavingsGroup.GroupStatus.ACTIVE) {
            throw new IllegalStateException("Cannot delete an active group");
        }

        savingsGroupRepository.delete(group);
    }

    public GroupResponse joinGroup(String groupId, String userId, String userName) {
        SavingsGroup group = savingsGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        // Check if group is open for joining
        if (!group.isOpen()) {
            throw new IllegalStateException("Group is not accepting new members");
        }

        // Check if group is full
        if (group.isFull()) {
            throw new IllegalStateException("Group is already full");
        }

        // Check if user is already a member
        if (group.hasMember(userId)) {
            throw new IllegalStateException("User is already a member of this group");
        }

        // Check if user already has a pending request
        if (group.hasPendingMember(userId)) {
            throw new IllegalStateException("User already has a pending request to join this group");
        }

        // Add to pending members
        group.getPendingMemberIds().add(userId);

        SavingsGroup updatedGroup = savingsGroupRepository.save(group);
        return GroupResponse.fromEntity(updatedGroup);
    }

    public GroupResponse respondToJoinRequest(String groupId, String userId, boolean approved, String organizerId) {
        SavingsGroup group = savingsGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        // Check if user is the organizer
        if (!group.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedException("Only the organizer can respond to join requests");
        }

        // Check if user has a pending request
        if (!group.hasPendingMember(userId)) {
            throw new IllegalStateException("User does not have a pending request to join this group");
        }

        // Remove from pending members
        group.getPendingMemberIds().remove(userId);

        // If approved, add to members
        if (approved) {
            // Check if group is full
            if (group.isFull()) {
                throw new IllegalStateException("Group is already full");
            }

            GroupMember newMember = GroupMember.builder()
                    .userId(userId)
                    .joinedAt(LocalDateTime.now())
                    .status(GroupMember.MemberStatus.ACTIVE)
                    .build();

            group.getMembers().add(newMember);
        }

        SavingsGroup updatedGroup = savingsGroupRepository.save(group);
        return GroupResponse.fromEntity(updatedGroup);
    }

    public List<GroupResponse> getOrganizerGroups(String organizerId) {
        return savingsGroupRepository.findByOrganizerId(organizerId).stream()
                .map(GroupResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public GroupResponse assignNextRecipient(String groupId, String organizerId) {
        SavingsGroup group = savingsGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        // Check if user is the organizer
        if (!group.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedException("Only the organizer can assign recipients");
        }

        // Check if group is active
        if (group.getStatus() != SavingsGroup.GroupStatus.ACTIVE) {
            if (group.getStatus() == SavingsGroup.GroupStatus.OPEN) {
                // If group is still open, start it
                group.setStatus(SavingsGroup.GroupStatus.ACTIVE);
                group.setCurrentCycle(1);

                if (group.getStartDate() == null) {
                    group.setStartDate(LocalDateTime.now());
                }

                // Calculate end date based on cycles and duration
                LocalDateTime endDate = group.getStartDate()
                        .plusMonths((long) group.getCycleDurationInMonths() * group.getMembers().size());
                group.setEndDate(endDate);
            } else {
                throw new IllegalStateException("Group is not in an active state");
            }
        }

        // Find eligible members who haven't received yet
        List<GroupMember> eligibleMembers = group.getMembers().stream()
                .filter(member -> member.getStatus() == GroupMember.MemberStatus.ACTIVE)
                .filter(member -> !member.hasReceivedInCurrentCycle(group.getCurrentCycle()))
                .collect(Collectors.toList());

        if (eligibleMembers.isEmpty()) {
            // Move to next cycle if all members have received
            group.setCurrentCycle(group.getCurrentCycle() + 1);

            // Check if all cycles completed
            if (group.getCurrentCycle() > group.getTotalCycles()) {
                group.setStatus(SavingsGroup.GroupStatus.COMPLETED);
                return GroupResponse.fromEntity(savingsGroupRepository.save(group));
            }

            // Reset for new cycle
            eligibleMembers = group.getMembers().stream()
                    .filter(member -> member.getStatus() == GroupMember.MemberStatus.ACTIVE)
                    .collect(Collectors.toList());
        }

        if (eligibleMembers.isEmpty()) {
            throw new IllegalStateException("No eligible members found for the current cycle");
        }

        // Select next recipient
        GroupMember selectedMember = eligibleMembers.get(0); // For simplicity, pick first eligible member
        selectedMember.getCyclesReceived().add(group.getCurrentCycle());
        group.setCurrentRecipientId(selectedMember.getUserId());

        SavingsGroup updatedGroup = savingsGroupRepository.save(group);
        return GroupResponse.fromEntity(updatedGroup);
    }

    public GroupResponse closeGroup(String groupId, String organizerId) {
        SavingsGroup group = savingsGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        // Check if user is the organizer
        if (!group.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedException("Only the organizer can close this group");
        }

        // Don't allow closing if already completed
        if (group.getStatus() == SavingsGroup.GroupStatus.COMPLETED) {
            throw new IllegalStateException("Group is already completed");
        }

        group.setStatus(SavingsGroup.GroupStatus.CLOSED);

        SavingsGroup updatedGroup = savingsGroupRepository.save(group);
        return GroupResponse.fromEntity(updatedGroup);
    }
}