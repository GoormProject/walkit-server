package life.walkit.server.walk.controller;

import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.walk.dto.enums.WalkResponse;
import life.walkit.server.walk.dto.request.WalkRequest;
import life.walkit.server.walk.dto.response.WalkEventResponse;
import life.walkit.server.walk.service.WalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// TODO: Swagger 표시
@RestController
@RequestMapping("/api/walks")
@RequiredArgsConstructor
public class WalkController {

    private final WalkService walkService;

    @PostMapping("/start")
    public ResponseEntity<BaseResponse> startWalk(@AuthenticationPrincipal CustomMemberDetails member) {

        return BaseResponse.toResponseEntity(
            WalkResponse.START_WALK_SUCCESS,
            walkService.startWalk(member.getMemberId())
        );
    }

    @PutMapping("/{walkId}/pause")
    public ResponseEntity<BaseResponse> pauseWalk(@PathVariable("walkId") Long walkId) {

        return BaseResponse.toResponseEntity(
            WalkResponse.PAUSE_WALK_SUCCESS,
            walkService.pauseWalk(walkId)
        );
    }

    @PutMapping("/{walkId}/resume")
    public ResponseEntity<BaseResponse> resumeWalk(@PathVariable("walkId") Long walkId) {

        return BaseResponse.toResponseEntity(
            WalkResponse.RESUME_WALK_SUCCESS,
            walkService.resumeWalk(walkId)
        );
    }

    @PutMapping("/{walkId}/end")
    public ResponseEntity<BaseResponse> endWalk(@PathVariable("walkId") Long walkId) {

        return BaseResponse.toResponseEntity(
            WalkResponse.END_WALK_SUCCESS,
            walkService.endWalk(walkId)
        );
    }

    @PostMapping("/new")
    public ResponseEntity<BaseResponse> createWalk(@RequestBody WalkRequest walkRequest) {

        return BaseResponse.toResponseEntity(
            WalkResponse.CREATE_WALK_SUCCESS,
            walkService.createWalk(walkRequest)
        );
    }

    @GetMapping("/walks")
    public ResponseEntity<BaseResponse> getWalkList(@AuthenticationPrincipal CustomMemberDetails member) {

        return BaseResponse.toResponseEntity(
            WalkResponse.GET_WALK_LIST_SUCCESS,
            walkService.getWalkList(member.getMemberId())
        );
    }

    @DeleteMapping("/{walkId}")
    public ResponseEntity<BaseResponse> deleteWalk(@PathVariable("walkId") Long walkId) {

        return BaseResponse.toResponseEntity(
            WalkResponse.DELETE_WALK_SUCCESS,
            walkService.deleteWalk(walkId)
        );
    }
}
