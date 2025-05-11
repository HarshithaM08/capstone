package com.savingsapp.groupservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "savings_groups")
public class SavingsGroup {

    @Id
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

    @Builder.Default
    private GroupStatus status = GroupStatus.OPEN;

    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();

    @Builder.Default
    private List<String> pendingMemberIds = new ArrayList<>();

    private int currentCycle;
    private int totalCycles;
    private String currentRecipientId;

    public enum GroupStatus {
        OPEN,       // Group is open for new members
        ACTIVE,     // Group has started cycles but not accepting new members
        COMPLETED,  // All cycles completed
        CLOSED      // Group was closed prematurely
    }

    public boolean isActive() {
        return status == GroupStatus.ACTIVE;
    }

    public boolean isOpen() {
        return status == GroupStatus.OPEN;
    }

    public boolean isFull() {
        return members.size() >= maxMembers;
    }

    public boolean hasMember(String userId) {
        return members.stream().anyMatch(member -> member.getUserId().equals(userId));
    }

    public boolean hasPendingMember(String userId) {
        return pendingMemberIds.contains(userId);
    }
}