package life.walkit.server.trail.controller;

import jakarta.validation.Valid;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.trail.dto.enums.TrailResponse;
import life.walkit.server.trail.dto.request.TrailCreateRequest;
import life.walkit.server.trail.dto.response.TrailCreateResponse;
import life.walkit.server.trail.service.TrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trails")
public class TrailController {

    private final TrailService trailService;

    @PostMapping("/new")
    public ResponseEntity<BaseResponse<TrailCreateResponse>> createTrail(@Valid TrailCreateRequest trailCreateRequest) {

        return BaseResponse.toResponseEntity(
            TrailResponse.TRAIL_CREATE_SUCCESS,
            trailService.createTrail(trailCreateRequest)
        );
    }
}
