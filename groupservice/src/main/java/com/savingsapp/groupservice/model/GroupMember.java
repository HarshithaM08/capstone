package com.savingsapp.groupservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {

    private String userId;
    private String name; // User's display name
    private LocalDateTime joinedAt;

    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;

    @Builder.Default
    private List<Integer> cyclesReceived = new ArrayList<>();

    private LocalDateTime lastContributionDate;

    public enum MemberStatus {
        ACTIVE,
        INACTIVE
    }

    public boolean hasReceivedInCurrentCycle(int currentCycle) {
        return cyclesReceived.contains(currentCycle);
    }
}