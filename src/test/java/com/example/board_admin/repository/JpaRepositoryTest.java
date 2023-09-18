package com.example.board_admin.repository;

import com.example.board_admin.domain.UserAccount;
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

    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(
            @Autowired UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("회원정보 select 테스트")
    @Test
    void givenUserAccounts_whenSelecting_thenWorksFine() {
        //given

        //when
        List<UserAccount> userAccounts = userAccountRepository.findAll();

        //then
        assertThat(userAccounts)
                .isNotNull()
                .hasSize(4);
    }

    @DisplayName("회원정보 insert 테스트")
    @Test
    void givenUserAccount_whenInserting_thenWorksFine() {
        //given
        long previousCount = userAccountRepository.count();
        UserAccount userAccount = UserAccount.of(
                "test",
                "testPassword",
                Set.of(RoleType.DEVELOPER),
                "test@1234",
                "nickname",
                "memo");
        //when
        userAccountRepository.save(userAccount);

        //then
        assertThat(userAccountRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("회원정보 update 테스트")
    @Test
    void givenUserAccountRoleType_whenUpdating_thenWorksFine() {
        //given
        UserAccount userAccount = userAccountRepository.getReferenceById("uno");
        userAccount.addRoleType(RoleType.DEVELOPER);
        userAccount.addRoleTypes(List.of(RoleType.USER, RoleType.USER));
        userAccount.removeRoleType(RoleType.ADMIN);

        //when
        UserAccount updatedUserAccount = userAccountRepository.saveAndFlush(userAccount);

        //then
        assertThat(updatedUserAccount)
                .hasFieldOrPropertyWithValue("userId", "uno")
                .hasFieldOrPropertyWithValue("roleTypes", Set.of(RoleType.DEVELOPER, RoleType.USER));
    }

    @DisplayName("회원정보 delete 테스트")
    @Test
    void givenUserAccount_whenDeleting_thenWorksFine() {
        //given
        long previousCount = userAccountRepository.count();
        UserAccount userAccount = userAccountRepository.getReferenceById("uno");

        //when
        userAccountRepository.delete(userAccount);

        //then
        assertThat(userAccountRepository.count()).isEqualTo(previousCount - 1);
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


