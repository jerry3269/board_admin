package com.example.board_admin.service;

import com.example.board_admin.domain.constant.RoleType;
import com.example.board_admin.dto.ArticleCommentDto;
import com.example.board_admin.dto.UserAccountDto;
import com.example.board_admin.dto.properties.ProjectProperties;
import com.example.board_admin.dto.response.ArticleCommentClientResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 댓글 관리")
class ArticleCommentManagementServiceTest {

//    @Disabled("실제 API 호출 결과 관찰용이므로 평상시엔 비활성화")
    @DisplayName("실제 API 호출 테스트")
    @SpringBootTest
    @Nested
    class RealApiTest {
        private final ArticleCommentManagementService sut;

        @Autowired
        public RealApiTest(ArticleCommentManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("댓글 API를 호출하면, 댓글들을 가져온다.")
        @Test
        void givenNothing_whenCallingArticleCommentsApi_thenReturnsArticleCommentList(){
            //given

            //when
            List<ArticleCommentDto> articleComments = sut.getArticleComments();
            //then
            System.out.println(articleComments.stream().findFirst());
            assertThat(articleComments).isNotNull();
        }
    }

    @DisplayName("API Mocking 테스트")
    @EnableConfigurationProperties(ProjectProperties.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(ArticleCommentManagementService.class)
    @Nested
    class RestTemplateTest {

        private final ArticleCommentManagementService sut;

        private final ProjectProperties projectProperties;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public RestTemplateTest(
                ArticleCommentManagementService sut,
                ProjectProperties projectProperties,
                MockRestServiceServer server,
                ObjectMapper mapper) {

            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }

        @DisplayName("댓글 API를 호출하면, 댓글들을 가져온다.")
        @Test
        void givenNothing_whenCallingArticleCommentsApi_thenReturnsArticleCommentList() throws Exception {
            //given
            ArticleCommentDto expectedArticle = createArticleCommentDto("글");
            ArticleCommentClientResponse expectedResponse = ArticleCommentClientResponse.of(List.of(expectedArticle));

            server.expect(requestTo(projectProperties.board().url() + "/api/articleComments?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));

            //when
            List<ArticleCommentDto> result = sut.getArticleComments();

            //then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticle.userAccount().nickname());
            server.verify();
        }

        @DisplayName("댓글을 ID와 함께 API 조회하면, 댓글을 가져온다.")
        @Test
        void givenArticleCommentId_whenCallingArticleCommentApi_thenReturnsArticleComment() throws Exception{
            //given
            Long articleCommentId = 1L;
            ArticleCommentDto expectedArticleComment = createArticleCommentDto("글");
            server.expect(requestTo(projectProperties.board().url() + "/api/articleComments/" + articleCommentId))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedArticleComment),
                            MediaType.APPLICATION_JSON
                    ));

            //when
            ArticleCommentDto result = sut.getArticleComment(articleCommentId);

            //then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("id", expectedArticleComment.id())
                    .hasFieldOrPropertyWithValue("content", expectedArticleComment.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticleComment.userAccount().nickname());
            server.verify();
        }

        @DisplayName("댓글을 ID와 함께 댓글 삭제 API를 호출하면, 댓글을 삭제한다.")
        @Test
        void givenArticleCommentId_whenCallingDeleteArticleCommentApi_thenDeletesArticleComment() throws Exception{
            //given
            Long articleCommentId = 1L;
            ArticleCommentDto expectedArticle = createArticleCommentDto("글");
            server.expect(requestTo(projectProperties.board().url() + "/api/articleComments/" + articleCommentId))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            //when
            sut.deleteArticleComment(articleCommentId);

            //then
            server.verify();
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
                    Set.of(RoleType.ADMIN),
                    "test@email",
                    "testNickname",
                    "testMemo"
            );
        }
    }
}
