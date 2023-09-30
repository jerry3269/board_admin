package com.example.board_admin.controller;

import com.example.board_admin.config.SecurityConfig;
import com.example.board_admin.dto.ArticleCommentDto;
import com.example.board_admin.dto.UserAccountDto;
import com.example.board_admin.service.UserAccountManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("컨트롤러 - 회원 관리")
@Import(SecurityConfig.class)
@WebMvcTest(UserAccountManagementController.class)
class UserAccountManagementControllerTest {

    private final MockMvc mvc;
    @MockBean
    private UserAccountManagementService userAccountManagementService;

    public UserAccountManagementControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[View][Get] 회원 관리 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingUserAccountManagementView_thenReturnsUserAccountManagementView() throws Exception {
        //given
        given(userAccountManagementService.getUserAccounts()).willReturn(List.of());

        //when && then
        mvc.perform(get("/management/user-accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("management/user-accounts"))
                .andExpect(model().attribute("userAccounts", List.of()));
        then(userAccountManagementService).should().getUserAccounts();
    }

    @DisplayName("[Data][Get] 회원 1개 - 정상 호출")
    @Test
    void givenUserId_whenRequestingUserAccount_thenReturnsUserAccount() throws Exception {
        //given
        String userId = "testId";
        UserAccountDto userAccountDto = createUserAccountDto();
        given(userAccountManagementService.getUserAccount(userId)).willReturn(userAccountDto);

        //when && then
        mvc.perform(get("/management/user-accounts/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.nickname").value(userAccountDto.nickname()));
        then(userAccountManagementService).should().getUserAccount(userId);
    }

    @DisplayName("[View][Post] 회원 삭제 - 정상 호출")
    @Test
    void givenUserId_whenRequestingDeletion_thenRedirectsToUserAccountManagementView() throws Exception {
        //given
        String userId = "testId";
        willDoNothing().given(userAccountManagementService).deleteUserAccount(userId);

        //when && then
        mvc.perform(post("/management/user-accounts/" + userId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/management/user-accounts"))
                .andExpect(redirectedUrl("/management/user-accounts"));
        then(userAccountManagementService).should().deleteUserAccount(userId);
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "testId",
                "test@email",
                "testNickname",
                "testMemo"
        );
    }
}
