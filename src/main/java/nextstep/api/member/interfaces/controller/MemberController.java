package nextstep.api.member.interfaces.controller;

import lombok.RequiredArgsConstructor;
import nextstep.api.member.application.MemberService;
import nextstep.api.member.application.dto.MemberRequest;
import nextstep.api.member.application.dto.MemberResponse;
import nextstep.api.member.domain.LoginMember;
import nextstep.common.annotation.AuthenticationPrincipal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<Void> createMember(@RequestBody MemberRequest request) {
        MemberResponse member = memberService.createMember(request);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).build();
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponse> findMember(@AuthenticationPrincipal LoginMember loginMember, @PathVariable Long id) {
        MemberResponse member = memberService.findMember(loginMember, id);
        return ResponseEntity.ok().body(member);
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<MemberResponse> updateMember(@AuthenticationPrincipal LoginMember loginMember, @PathVariable Long id, @RequestBody MemberRequest param) {
        memberService.updateMember(loginMember, id, param);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<MemberResponse> deleteMember(@AuthenticationPrincipal LoginMember loginMember, @PathVariable Long id) {
        memberService.deleteMember(loginMember, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members/me")
    public ResponseEntity<MemberResponse> findMemberOfMine(@AuthenticationPrincipal LoginMember loginMember) {
        MemberResponse memberResponse = memberService.findMe(loginMember);
        return ResponseEntity.ok().body(memberResponse);
    }
}
