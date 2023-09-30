package com.example.board_admin.repository;

import com.example.board_admin.domain.AdminAccount;
import com.example.board_admin.domain.constant.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA 연결 테스트")
@DataJpaTest
class JpaRepositoryTest {

    private final AdminAccountRepository adminAccountRepository;

    public JpaRepositoryTest(
            @Autowired AdminAccountRepository adminAccountRepository) {
        this.adminAccountRepository = adminAccountRepository;
    }

    @DisplayName("회원정보 select 테스트")
    @Test
    void givenAdminAccounts_whenSelecting_thenWorksFine() {
        //given

        //when
        List<AdminAccount> adminAccounts = adminAccountRepository.findAll();

        //then
        assertThat(adminAccounts)
                .isNotNull()
                .hasSize(4);
    }

    @DisplayName("회원정보 insert 테스트")
    @Test
    void givenAdminAccount_whenInserting_thenWorksFine() {
        //given
        long previousCount = adminAccountRepository.count();
        AdminAccount adminAccount = AdminAccount.of(
                "test",
                "testPassword",
                Set.of(RoleType.DEVELOPER),
                "test@1234",
                "nickname",
                "memo");
        //when
        adminAccountRepository.save(adminAccount);

        //then
        assertThat(adminAccountRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("회원정보 update 테스트")
    @Test
    void givenAdminAccountRoleType_whenUpdating_thenWorksFine() {
        //given
        AdminAccount adminAccount = adminAccountRepository.getReferenceById("uno");
        adminAccount.addRoleType(RoleType.DEVELOPER);
        adminAccount.addRoleTypes(List.of(RoleType.USER, RoleType.USER));
        adminAccount.removeRoleType(RoleType.ADMIN);

        //when
        AdminAccount updatedAdminAccount = adminAccountRepository.saveAndFlush(adminAccount);

        //then
        assertThat(updatedAdminAccount)
                .hasFieldOrPropertyWithValue("userId", "uno")
                .hasFieldOrPropertyWithValue("roleTypes", Set.of(RoleType.DEVELOPER, RoleType.USER));
    }

    @DisplayName("회원정보 delete 테스트")
    @Test
    void givenAdminAccount_whenDeleting_thenWorksFine() {
        //given
        long previousCount = adminAccountRepository.count();
        AdminAccount adminAccount = adminAccountRepository.getReferenceById("uno");

        //when
        adminAccountRepository.delete(adminAccount);

        //then
        assertThat(adminAccountRepository.count()).isEqualTo(previousCount - 1);
    }

    @EnableJpaAuditing
    @TestConfiguration
    static class TestJpaConfig{
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("uno");
        }
    }
}


