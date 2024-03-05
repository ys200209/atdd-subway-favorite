package nextstep.favorite.application;

import nextstep.favorite.application.dto.FavoriteRequest;
import nextstep.favorite.application.dto.FavoriteResponse;
import nextstep.favorite.domain.Favorite;
import nextstep.favorite.domain.FavoriteRepository;
import nextstep.member.domain.LoginMember;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    private MemberRepository memberRepository;
    // 질문:
    // 이렇게 다른 도메인 엔티티가 필요할 때, 다른 도메인의 Service 계층과 Repository 계층 중에서 어느 계층을 의존받는 것이 좋을까요??
    // 조금이라도 덜 의존적이기 위해서는 Repository 계층을 직접적으로 의존하는 것이 좋다고 생각이 드는데,
    // 그렇게 되면 만약 존재하지 않는 사용자에 대한 조회를 시도할 때에 대한 예외 처리 코드를 한번 더 작성해주어야 할 것 같아요.
    // 그럼 중복 코드가 발생할텐데, 이 관점에서는 차라리 미리 예외 처리까지 되어있는 해당 도메인의 Service 계층을 의존하는 것이 더 좋을 것 같은데.. 어떻게 생각하시나요?

    private FavoriteRepository favoriteRepository;

    public FavoriteService(MemberRepository memberRepository, FavoriteRepository favoriteRepository) {
        this.memberRepository = memberRepository;
        this.favoriteRepository = favoriteRepository;
    }

    /**
     * TODO: LoginMember 를 추가로 받아서 FavoriteRequest 내용과 함께 Favorite 를 생성합니다.ㄴ
     *
     * @param request
     * @return 생성된 Favorite
     */
    public Favorite createFavorite(LoginMember loginMember, FavoriteRequest request) {
        Member member = memberRepository.findByEmail(loginMember.getEmail()).orElseThrow(RuntimeException::new);

        Favorite favorite = new Favorite(member, request.getSource(), request.getTarget());
        favoriteRepository.save(favorite);
        return favorite;
    }

    /**
     * TODO: StationResponse 를 응답하는 FavoriteResponse 로 변환해야 합니다.
     *
     * @return
     */
    public List<FavoriteResponse> findFavorites() {
        List<Favorite> favorites = favoriteRepository.findAll();
        return null;
    }

    /**
     * TODO: 요구사항 설명에 맞게 수정합니다.
     * @param id
     */
    public void deleteFavorite(Long id) {
        favoriteRepository.deleteById(id);
    }
}
