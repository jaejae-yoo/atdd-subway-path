package wooteco.subway.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull(message = "상행 역을 입력해주세요.")
    private Long upStationId;
    @NotNull(message = "하행 역을 입력해주세요.")
    private Long downStationId;
    @NotNull(message = "거리를 입력해주세요.")
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
