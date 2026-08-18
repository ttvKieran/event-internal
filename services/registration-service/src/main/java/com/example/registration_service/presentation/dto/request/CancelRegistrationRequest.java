package com.example.registration_service.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelRegistrationRequest {

    @NotBlank(message = "Lý do hủy vé không được để trống")
    private String reason;

}
