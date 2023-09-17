package com.example.board_admin.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    USER("ROLE_USER"),
    MANAGER("ROLE_MANAGER"),
    DEVELOPER("ROLE_DEVELOPER"),
    ADMIN("ROLE_ADMIN");

    private final String roleName;
}
