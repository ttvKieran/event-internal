package com.example.iam_service.application.port.out;

import com.example.iam_service.application.dto.message.EmployeeEventPayload;

public interface IamMessagePort {
    void publishEmployeeEvent(String eventType, EmployeeEventPayload payload);
}
