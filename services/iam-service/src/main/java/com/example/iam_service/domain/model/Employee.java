package com.example.iam_service.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Employee {
    private String id;
    private String fullname;
    private String email;
    private String employeeCode;
    private Department department;
    private Role role;
    private String status;
    private String password;
    private String refreshToken;

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    public void updateEmployee(String fullname, String email, String password) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }

    public void lockEmployee() {
        this.status = "INACTIVE";
    }

    public void unlockEmployee() {
        this.status = "ACTIVE";
    }
}
