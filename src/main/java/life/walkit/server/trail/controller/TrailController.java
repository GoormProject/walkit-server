package life.walkit.server.trail.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import life.walkit.server.global.response.BaseResponse;
import life.walkit.server.trail.dto.enums.TrailResponse;
import life.walkit.server.trail.dto.request.TrailCreateRequest;
import life.walkit.server.trail.dto.response.TrailCreateResponse;
import life.walkit.server.trail.dto.response.TrailDetailResponse;
import life.walkit.server.trail.dto.response.TrailListResponse;
import life.walkit.server.trail.service.TrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "산책로",
    description = "산책로 관련 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trails")
public class TrailController {

    private final TrailService trailService;

    @Operation(
        summary = "산책로 생성",
        description = "새로운 산책로를 등록합니다."
    )
    @PostMapping("/new")
    public ResponseEntity<BaseResponse<TrailCreateResponse>> createTrail(@Valid @RequestBody TrailCreateRequest trailCreateRequest) {

        return BaseResponse.toResponseEntity(
            TrailResponse.TRAIL_CREATE_SUCCESS,
            trailService.createTrail(trailCreateRequest)
        );
    }

    @Operation(
        summary = "산책로 상세 조회",
        description = "특정 산책로의 상세 정보를 조회합니다."
    )
    @GetMapping("/{trailId}")
    public ResponseEntity<BaseResponse<TrailDetailResponse>> getTrailDetail(@PathVariable Long trailId) {

        return BaseResponse.toResponseEntity(
            TrailResponse.TRAIL_DETAIL_SELECT_SUCCESS,
            trailService.getTrailDetail(trailId)
        );
    }

    @Operation(
        summary = "산책로 목록 조회",
        description = "페이징 처리된 산책로 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<Page<TrailListResponse>>> getTrailList(
        @RequestParam(
            value = "page",
            defaultValue = "0"
        ) int page
    ) {
        return BaseResponse.toResponseEntity(
            TrailResponse.GET_TRAIL_LIST_SUCCESS,
            trailService.getTrailList(page)
        );
    }
}
