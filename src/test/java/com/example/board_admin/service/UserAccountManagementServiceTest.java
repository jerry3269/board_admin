package com.example.board_admin.service;

import com.example.board_admin.dto.UserAccountDto;
import com.example.board_admin.dto.properties.ProjectProperties;
import com.example.board_admin.dto.response.UserAccountClientResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 회원 관리")
class UserAccountManagementServiceTest {

//    @Disabled("실제 API 호출 결과 관찰용이므로 평상시엔 비활성화")
    @DisplayName("실제 API 호출 테스트")
    @SpringBootTest
    @Nested
    class RealApiTest {
        private final UserAccountManagementService sut;

        @Autowired
        public RealApiTest(UserAccountManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("회원 API를 호출하면, 회원들을 가져온다.")
        @Test
        void givenNothing_whenCallingUserAccountsApi_thenReturnsUserAccountList(){
            //given

            //when
            List<UserAccountDto> userAccounts = sut.getUserAccounts();
            //then
            System.out.println(userAccounts.stream().findFirst());
            assertThat(userAccounts).isNotNull();
        }
    }

    @DisplayName("API Mocking 테스트")
    @EnableConfigurationProperties(ProjectProperties.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(UserAccountManagementService.class)
    @Nested
    class RestTemplateTest {

        private final UserAccountManagementService sut;

        private final ProjectProperties projectProperties;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public RestTemplateTest(
                UserAccountManagementService sut,
                ProjectProperties projectProperties,
                MockRestServiceServer server,
                ObjectMapper mapper) {

            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }

        @DisplayName("회원 API를 호출하면, 회원들을 가져온다.")
        @Test
        void givenNothing_whenCallingUserAccountsApi_thenReturnsUserAccountList() throws Exception {
            //given
            UserAccountDto expectedUserAccount = createUserAccountDto();
            UserAccountClientResponse expectedResponse = UserAccountClientResponse.of(List.of(expectedUserAccount));

            server.expect(requestTo(projectProperties.board().url() + "/api/userAccounts?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));

            //when
            List<UserAccountDto> result = sut.getUserAccounts();

            //then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("userId", expectedUserAccount.userId())
                    .hasFieldOrPropertyWithValue("email", expectedUserAccount.email())
                    .hasFieldOrPropertyWithValue("nickname", expectedUserAccount.nickname())
                    .hasFieldOrPropertyWithValue("memo", expectedUserAccount.memo());
            server.verify();
        }

        @DisplayName("회원을 ID와 함께 API 조회하면, 회원을 가져온다.")
        @Test
        void givenUserAccountId_whenCallingUserAccountApi_thenReturnsUserAccount() throws Exception{
            //given
            String userId = "testId";
            UserAccountDto expectedUserAccount = createUserAccountDto();
            server.expect(requestTo(projectProperties.board().url() + "/api/userAccounts/" + userId))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedUserAccount),
                            MediaType.APPLICATION_JSON
                    ));

            //when
            UserAccountDto result = sut.getUserAccount(userId);

            //then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("userId", expectedUserAccount.userId())
                    .hasFieldOrPropertyWithValue("email", expectedUserAccount.email())
                    .hasFieldOrPropertyWithValue("nickname", expectedUserAccount.nickname())
                    .hasFieldOrPropertyWithValue("memo", expectedUserAccount.memo());
            server.verify();
        }

        @DisplayName("회원을 ID와 함께 댓글 삭제 API를 호출하면, 회원을 삭제한다.")
        @Test
        void givenUserAccountId_whenCallingDeleteUserAccountApi_thenDeletesUserAccount() throws Exception{
            //given
            String userId = "testId";
            UserAccountDto expectedArticle = createUserAccountDto();
            server.expect(requestTo(projectProperties.board().url() + "/api/userAccounts/" + userId))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            //when
            sut.deleteUserAccount(userId);

            //then
            server.verify();
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
}
