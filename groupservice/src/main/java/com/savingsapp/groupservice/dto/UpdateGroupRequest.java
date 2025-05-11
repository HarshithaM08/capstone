package com.savingsapp.groupservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupRequest {

    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private BigDecimal contributionAmount;

    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    private String currency;

    @Min(value = 1, message = "Cycle duration must be at least 1 month")
    @Max(value = 12, message = "Cycle duration cannot exceed 12 months")
    private Integer cycleDurationInMonths;

    private LocalDateTime startDate;
}