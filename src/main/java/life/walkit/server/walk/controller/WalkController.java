package life.walkit.server.walk.controller;

import life.walkit.server.auth.dto.CustomMemberDetails;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.walk.dto.enums.WalkResponse;
import life.walkit.server.walk.repository.WalkRepository;
import life.walkit.server.walk.service.WalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller("/api/walks")
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
}
