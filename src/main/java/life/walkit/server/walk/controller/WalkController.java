package life.walkit.server.walk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.walk.dto.enums.WalkResponse;
import life.walkit.server.walk.dto.request.WalkRequest;
import life.walkit.server.walk.dto.response.WalkCreateResponse;
import life.walkit.server.walk.dto.response.WalkDeleteResponse;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.dto.response.WalkListResponse;
import life.walkit.server.walk.service.WalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
    name = "산책 기록",
    description = "산책 기록 관련 API"
)
@RestController
@RequestMapping("/api/walks")
@RequiredArgsConstructor
public class WalkController {

    private final WalkService walkService;

    @Operation(
        summary = "산책 기록 시작",
        description = "새로운 산책 기록을 시작하고, 생성된 walkId와 eventId를 반환합니다."
    )
    @PostMapping("/start")
    public ResponseEntity<BaseResponse<WalkEventResponse>> startWalk(@AuthenticationPrincipal CustomMemberDetails member) {
        return BaseResponse.toResponseEntity(
            WalkResponse.START_WALK_SUCCESS,
            walkService.startWalk(member.getMemberId())
        );
    }

    @Operation(
        summary = "산책 기록 일시정지",
        description = "진행 중인 산책을 일시정지합니다."
    )
    @PutMapping("/{walkId}/pause")
    public ResponseEntity<BaseResponse<WalkEventResponse>> pauseWalk(@PathVariable("walkId") Long walkId) {
        return BaseResponse.toResponseEntity(
            WalkResponse.PAUSE_WALK_SUCCESS,
            walkService.pauseWalk(walkId)
        );
    }

    @Operation(
        summary = "산책 기록 재개",
        description = "일시정지된 산책을 다시 시작합니다."
    )
    @PutMapping("/{walkId}/resume")
    public ResponseEntity<BaseResponse<WalkEventResponse>> resumeWalk(@PathVariable("walkId") Long walkId) {
        return BaseResponse.toResponseEntity(
            WalkResponse.RESUME_WALK_SUCCESS,
            walkService.resumeWalk(walkId)
        );
    }

    @Operation(
        summary = "산책 기록 종료",
        description = "진행 중인 산책을 최종 종료합니다."
    )
    @PutMapping("/{walkId}/end")
    public ResponseEntity<BaseResponse<WalkEventResponse>> endWalk(@PathVariable("walkId") Long walkId) {
        return BaseResponse.toResponseEntity(
            WalkResponse.END_WALK_SUCCESS,
            walkService.endWalk(walkId)
        );
    }

    @Operation(
        summary = "산책 기록 저장",
        description = "종료된 산책의 상세 정보(경로, 시간, 거리 등)를 저장합니다."
    )
    @PostMapping("/new")
    public ResponseEntity<BaseResponse<WalkCreateResponse>> createWalk(@Valid @RequestBody WalkRequest walkRequest) {
        return BaseResponse.toResponseEntity(
            WalkResponse.CREATE_WALK_SUCCESS,
            walkService.createWalk(walkRequest)
        );
    }

    @Operation(
        summary = "산책 기록 목록 조회",
        description = "자신이 기록한 모든 산책 기록의 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<WalkListResponse>>> getWalkList(@AuthenticationPrincipal CustomMemberDetails member) {
        return BaseResponse.toResponseEntity(
            WalkResponse.GET_WALK_LIST_SUCCESS,
            walkService.getWalkList(member.getMemberId())
        );
    }

    @Operation(
        summary = "산책 기록 삭제",
        description = "특정 산책 기록을 삭제합니다."
    )
    @DeleteMapping("/{walkId}")
    public ResponseEntity<BaseResponse<WalkDeleteResponse>> deleteWalk(@PathVariable("walkId") Long walkId) {
        return BaseResponse.toResponseEntity(
            WalkResponse.DELETE_WALK_SUCCESS,
            walkService.deleteWalk(walkId)
        );
    }
}

