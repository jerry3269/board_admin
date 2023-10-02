package com.example.board_admin.controller;

import com.example.board_admin.config.TestSecurityConfig;
import com.example.board_admin.dto.ArticleCommentDto;
import com.example.board_admin.dto.UserAccountDto;
import com.example.board_admin.service.ArticleCommentManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("컨트롤러 - 댓글 관리")
@Import(TestSecurityConfig.class)
@WebMvcTest(ArticleCommentManagementController.class)
class ArticleCommentManagementControllerTest {
    private final MockMvc mvc;
    @MockBean private ArticleCommentManagementService articleCommentManagementService;

    public ArticleCommentManagementControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[View][Get] 댓글 관리 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticleCommentsManagementView_thenReturnsArticleCommentsManagementView() throws Exception {
        //given
        given(articleCommentManagementService.getArticleComments()).willReturn(List.of());

        //when && then
        mvc.perform(get("/management/article-comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("management/article-comments"))
                .andExpect(model().attribute("comments", List.of()));
        then(articleCommentManagementService).should().getArticleComments();
    }

    @DisplayName("[Data][Get] 댓글 1개 - 정상 호출")
    @Test
    void givenArticleCommentId_whenRequestingArticleComment_thenReturnsArticleComment() throws Exception {
        //given
        Long articleCommentId = 1L;
        ArticleCommentDto articleCommentDto = createArticleCommentDto("content");
        given(articleCommentManagementService.getArticleComment(articleCommentId)).willReturn(articleCommentDto);

        //when && then
        mvc.perform(get("/management/article-comments/" + articleCommentId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(articleCommentId))
                .andExpect(jsonPath("$.content").value(articleCommentDto.content()))
                .andExpect(jsonPath("$.userAccount.nickname").value(articleCommentDto.userAccount().nickname()));
        then(articleCommentManagementService).should().getArticleComment(articleCommentId);
    }

    @DisplayName("[View][Post] 댓글 삭제 - 정상 호출")
    @Test
    void givenArticleCommentId_whenRequestingDeletion_thenRedirectsToArticleCommentManagementView() throws Exception {
        //given
        Long articleCommentId = 1L;
        willDoNothing().given(articleCommentManagementService).deleteArticleComment(articleCommentId);

        //when && then
        mvc.perform(post("/management/article-comments/" + articleCommentId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/management/article-comments"))
                .andExpect(redirectedUrl("/management/article-comments"));
        then(articleCommentManagementService).should().deleteArticleComment(articleCommentId);
    }

    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                null,
                content,
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
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
