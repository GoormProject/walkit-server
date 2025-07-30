package life.walkit.server.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.friend.dto.FriendRequestResponseDTO;
import life.walkit.server.friend.dto.FriendResponseDTO;
import life.walkit.server.friend.dto.ReceivedFriendResponse;
import life.walkit.server.friend.dto.SentFriendResponse;
import life.walkit.server.friend.enums.FriendRequestResponse;
import life.walkit.server.friend.enums.FriendResponse;
import life.walkit.server.friend.service.FriendService;
import life.walkit.server.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "친구", description = "친구 API")
@SecurityRequirement(name = "cookieAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 요청 생성", description = "다른 사용자에게 친구 요청을 보냅니다.")
    @PostMapping("/request")
    public ResponseEntity<BaseResponse<FriendRequestResponseDTO>> sendFriendRequest(
            @AuthenticationPrincipal CustomMemberDetails member,
            @RequestParam String targetNickname
    ) {
        return BaseResponse.toResponseEntity(
                FriendRequestResponse.REQUEST_SUCCESS,
                friendService.sendFriendRequest(member.getMemberId(), targetNickname)
        );
    }

    @GetMapping("/request/sent")
    @Operation(summary = "친구 요청 발신 목록 조회", description = "내가 보낸 친구 요청 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<List<SentFriendResponse>>> getSentFriendRequests(
            @AuthenticationPrincipal CustomMemberDetails member
    ) {
        return BaseResponse.toResponseEntity(
                FriendRequestResponse.SENT_LIST_SUCCESS,
                friendService.getSentFriendRequests(member.getMemberId())
        );
    }

    @GetMapping("/request/received")
    @Operation(summary = "친구 요청 수신 목록 조회", description = "내가 받은 친구 요청 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<List<ReceivedFriendResponse>>> getReceivedFriendRequests(
            @AuthenticationPrincipal CustomMemberDetails member
    ) {
        return BaseResponse.toResponseEntity(
                FriendRequestResponse.RECEIVED_LIST_SUCCESS,
                friendService.getReceivedFriendRequests(member.getMemberId())
        );
    }

    @PatchMapping("/request/{friendRequestId}")
    @Operation(summary = "친구 요청 승인", description = "친구 요청을 승인합니다.")
    public ResponseEntity<BaseResponse<Void>> approveFriendRequest(
            @AuthenticationPrincipal CustomMemberDetails member,
            @PathVariable Long friendRequestId
    ) {
        friendService.approveFriendRequest(friendRequestId, member.getMemberId());
        return BaseResponse.toResponseEntity(FriendRequestResponse.APPROVED_SUCCESS);
    }

    @DeleteMapping("/requests/{friendRequestId}")
    @Operation(summary = "친구 요청 거절", description = "친구 요청을 거절합니다.")
    public ResponseEntity<BaseResponse<Void>> rejectFriendRequest(
            @AuthenticationPrincipal CustomMemberDetails member,
            @PathVariable Long friendRequestId
    ) {
        friendService.rejectFriendRequest(friendRequestId, member.getMemberId());
        return BaseResponse.toResponseEntity(FriendRequestResponse.REJECTED_SUCCESS);
    }

    @GetMapping
    @Operation(summary = "친구 목록 조회", description = "현재 사용자의 친구 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<List<FriendResponseDTO>>> getFriends(
            @AuthenticationPrincipal CustomMemberDetails member
    ) {
        return BaseResponse.toResponseEntity(
                FriendResponse.LIST_SUCCESS,
                friendService.getFriends(member.getMemberId()));
    }

}
