package life.walkit.server.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "회원", description = "회원 API")
@RequestMapping("/api/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "이름과 닉네임, 프로필 이미지, 이메일을 조회합니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<BaseResponse> getProfile(@PathVariable("memberId") Long memberId,
                                                   @AuthenticationPrincipal CustomMemberDetails member) {

        if (!memberId.equals(member.getMemberId())) {
            throw new MemberException(MemberErrorCode.MEMBER_FORBIDDEN);
        }

        return BaseResponse.toResponseEntity(
                MemberResponse.GET_PROFILE_SUCCESS,
                memberService.getProfile(memberId)
        );
    }

    @Operation(summary = "프로필 수정", description = "이름과 닉네임, 프로필 이미지를 수정합니다.")
    @PutMapping("/{memberId}")
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
