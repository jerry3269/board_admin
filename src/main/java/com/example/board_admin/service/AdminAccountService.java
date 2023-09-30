package com.example.board_admin.service;

import com.example.board_admin.domain.constant.RoleType;
import com.example.board_admin.dto.AdminAccountDto;
import com.example.board_admin.repository.AdminAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminAccountService {

    private final AdminAccountRepository adminAccountRepository;


    public Optional<AdminAccountDto> searchUser(String username) {
        return Optional.empty();
    }

    @Transactional
    public AdminAccountDto saveUser(String username, String userPassword, Set<RoleType> roleTypes, String email, String nickname, String memo){
        return null;
    }

    public List<AdminAccountDto> users() {
        return List.of();
    }

    @Transactional
    public void deleteUser(String username) {

    }

}
