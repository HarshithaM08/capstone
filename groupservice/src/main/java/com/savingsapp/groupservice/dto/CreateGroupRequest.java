package com.savingsapp.groupservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Contribution amount is required")
    @Positive(message = "Contribution amount must be greater than zero")
    private BigDecimal contributionAmount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    private String currency;

    @Min(value = 1, message = "Cycle duration must be at least 1 month")
    @Max(value = 12, message = "Cycle duration cannot exceed 12 months")
    private int cycleDurationInMonths;

    @Min(value = 2, message = "Group must have at least 2 members")
    @Max(value = 50, message = "Group cannot exceed 50 members")
    private int maxMembers;

    private LocalDateTime startDate;
}