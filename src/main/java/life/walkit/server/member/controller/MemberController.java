package life.walkit.server.member.controller;

import jakarta.validation.Valid;
import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.member.dto.ProfileRequest;
import life.walkit.server.member.dto.enums.MemberResponse;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/{memberId}")
    public ResponseEntity<BaseResponse> updateProfile(@PathVariable Long memberId,
                                                      @AuthenticationPrincipal CustomMemberDetails member,
                                                      @Valid @RequestPart("data") ProfileRequest request,
                                                      @RequestPart("profileImage") MultipartFile profileImage) {

        if (!memberId.equals(member.getMemberId())) {
            throw new MemberException(MemberErrorCode.MEMBER_FORBIDDEN);
        }

        return BaseResponse.toResponseEntity(
                MemberResponse.UPDATE_PROFILE_SUCCESS,
                memberService.updateProfile(memberId, request, profileImage)
        );
    }
}
