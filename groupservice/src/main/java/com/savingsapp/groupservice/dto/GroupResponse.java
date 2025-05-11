package com.savingsapp.groupservice.dto;

import com.savingsapp.groupservice.model.GroupMember;
import com.savingsapp.groupservice.model.SavingsGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {
    private String id;
    private String name;
    private String description;
    private String organizerId;
    private BigDecimal contributionAmount;
    private String currency;
    private int cycleDurationInMonths;
    private int maxMembers;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SavingsGroup.GroupStatus status;
    private List<GroupMember> members;
    private List<String> pendingMemberIds;
    private int currentCycle;
    private int totalCycles;
    private String currentRecipientId;

    public static GroupResponse fromEntity(SavingsGroup group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .organizerId(group.getOrganizerId())
                .contributionAmount(group.getContributionAmount())
                .currency(group.getCurrency())
                .cycleDurationInMonths(group.getCycleDurationInMonths())
                .maxMembers(group.getMaxMembers())
                .createdAt(group.getCreatedAt())
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .status(group.getStatus())
                .members(group.getMembers())
                .pendingMemberIds(group.getPendingMemberIds())
                .currentCycle(group.getCurrentCycle())
                .totalCycles(group.getTotalCycles())
                .currentRecipientId(group.getCurrentRecipientId())
                .build();
    }
}