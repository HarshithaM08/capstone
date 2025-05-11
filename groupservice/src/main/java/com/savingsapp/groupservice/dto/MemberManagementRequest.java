package com.savingsapp.groupservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberManagementRequest {

    @NotNull(message = "Response is required")
    private Boolean approved;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String message;
}