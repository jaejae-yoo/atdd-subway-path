package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PathServiceTest {

    @Autowired
    private PathService pathService;

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("최단 경로를 찾는다.")
    void findShortestPath() {
        StationResponse firstStation = stationService.save(new StationRequest("역삼역"));
        StationResponse secondStation = stationService.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10, 100);
        lineService.save(line);

        PathResponse shortestPath = pathService.findShortestPath(new PathRequest(firstStation.getId(), secondStation.getId(), 20L));
        PathResponse expected = new PathResponse(List.of(new Station(1L, "역삼역"), new Station(2L, "삼성역")), 10, 1450);
        assertThat(shortestPath).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}