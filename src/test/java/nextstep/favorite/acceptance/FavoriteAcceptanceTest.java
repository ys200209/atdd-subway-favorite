package nextstep.favorite.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import nextstep.utils.AcceptanceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("즐겨찾기 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "admin@email.com";
    public static final String PASSWORD = "password";
    public static final Integer AGE = 20;

    private Long 신분당선;
    private Long 교대역;
    private Long 신논현역;
    private Long 양재역;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(교대역, 신논현역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 양재역));
    }

    /**
     * TODO:
     * 1. 요구사항에 대한 인수 조건을 정의 (인수 조건은 테스트 코드 상단에 주석으로 정의)
     * 2. 인수 조건을 검증하는 테스트 코드 작성
     * 3. 테스트 코드를 통과하는 비즈니스 코드 작성
     *
     * 예외:
     * - 로그인이 필요한 API 요청 시 유효하지 않은 경우
     * - 존재하지 않는 경로인 경우
     * - 등등

    /**
     * When 즐겨찾기를 생성하면
     * Then 201 상태코드가 응답된다.
     */
    @DisplayName("즐겨찾기를 생성한다.")
    @Test
    void createFavorites() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("source", 교대역 + "");
        params.put("target", 양재역 + "");

        // when
        ExtractableResponse<Response> response = FavoriteSteps.즐겨찾기_생성_요청(params, getAccessToken());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 교대역 - 양재역 즐겨찾기가 등록하고
     * When 즐겨찾기 목록을 조회하면
     * Then 즐겨찾기 목록이 조회된다.
     */
    @DisplayName("즐겨찾기 목록을 조회한다.")
    @Test
    void getFavorites() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("source", 교대역 + "");
        params.put("target", 양재역 + "");
        String accessToken = getAccessToken();
        FavoriteSteps.즐겨찾기_생성_요청(params, accessToken);

        // when
        ExtractableResponse<Response> response = FavoriteSteps.즐겨찾기_목록_조회_요청(accessToken);
        Long sourceId = response.jsonPath().getLong("[0].source.id");
        Long targetId = response.jsonPath().getLong("[0].target.id");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(sourceId).isEqualTo(1L);
        assertThat(targetId).isEqualTo(3L);
    }

    public String getAccessToken() {
        memberRepository.save(new Member(EMAIL, PASSWORD, AGE)); // 회원가입

        Map<String, String> params = new HashMap<>();
        params.put("email", EMAIL);
        params.put("password", PASSWORD);

        ExtractableResponse<Response> response = RestAssured.given().log().all() // 로그인 (post)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        return response.jsonPath().getString("accessToken"); // 발급된 Access 토큰 가져오기
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }
}
