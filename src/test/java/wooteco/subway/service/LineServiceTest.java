package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.ClientException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationJdbcDao stationJdbcDao;

    @DisplayName("노선 저장")
    @Test
    void save() {
        Station firstStation = stationJdbcDao.save(new Station("역삼역"));
        Station secondStation = stationJdbcDao.save(new Station("삼성역"));

        LineRequest line = new LineRequest("4호선", "green", firstStation.getId(), secondStation.getId(), 10, 100);
        LineResponse newLine = lineService.save(line);

        assertThat(line.getName()).isEqualTo(newLine.getName());
    }

    @DisplayName("중복된 노선 저장시 예외")
    @Test
    void duplicateLine() {
        Station firstStation = stationJdbcDao.save(new Station("역삼역"));
        Station secondStation = stationJdbcDao.save(new Station("삼성역"));

        LineRequest line = new LineRequest("3호선", "red", firstStation.getId(), secondStation.getId(), 10, 100);
        LineRequest dupLine = new LineRequest("3호선", "red", firstStation.getId(), secondStation.getId(), 10, 100);
        lineService.save(line);

        assertThatThrownBy(() -> lineService.save(dupLine))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("이미 등록된 지하철노선입니다.");
    }

    @DisplayName("노선 정보 전체 조회")
    @Test
    void findAll() {
        Station firstStation = stationJdbcDao.save(new Station("역삼역"));
        Station secondStation = stationJdbcDao.save(new Station("삼성역"));

        lineService.save(new LineRequest("5호선", "red", firstStation.getId(), secondStation.getId(), 10, 100));
        lineService.save(new LineRequest("7호선", "red", firstStation.getId(), secondStation.getId(), 10, 100));

        List<LineResponse> lines = lineService.findAll();
        lines.stream()
                .filter(line -> line.getName().equals("5호선"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("노선 정보가 없습니다."));
    }

    @Test
    @DisplayName("노선 정보 조회")
    void find() {
        Station firstStation = stationJdbcDao.save(new Station("역삼역"));
        Station secondStation = stationJdbcDao.save(new Station("삼성역"));

        LineRequest line = new LineRequest("4호선", "red", firstStation.getId(), secondStation.getId(), 10, 100);
        LineResponse newLine = lineService.save(line);

        assertThat(lineService.findById(newLine.getId()).getName()).isEqualTo(line.getName());
    }

    @Test
    @DisplayName("노선 정보 조회 예외 - 존재하지 않는 노선")
    void checkNotExistFindLine() {
        assertThatThrownBy(() -> lineService.findById(0L))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("노선 정보 삭제")
    void delete() {
        Station firstStation = stationJdbcDao.save(new Station("역삼역"));
        Station secondStation = stationJdbcDao.save(new Station("삼성역"));

        LineRequest line = new LineRequest("4호선", "red", firstStation.getId(),
                secondStation.getId(), 10, 100);
        LineResponse newLine = lineService.save(line);

        assertThat(lineService.delete(newLine.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("노선 정보 삭제 예외 - 존재하지 않는 노선")
    void checkNotExistDeleteLine() {
        assertThatThrownBy(() -> lineService.delete(0L))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("노선 정보 업데이트")
    void update() {
        Station firstStation = stationJdbcDao.save(new Station("역삼역"));
        Station secondStation = stationJdbcDao.save(new Station("삼성역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10, 100);
        LineResponse newLine = lineService.save(line);

        assertThat(lineService.update(newLine.getId(), new LineRequest("7호선", "red", firstStation.getId(),
                secondStation.getId(), 10, 100))).isEqualTo(1);
    }

    @Test
    @DisplayName("노선 정보 업데이트 예외 - 이미 존재하는 노선으로 업데이트")
    void updateDuplicateLine() {
        Station firstStation = stationJdbcDao.save(new Station("역삼역"));
        Station secondStation = stationJdbcDao.save(new Station("삼성역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10, 100);
        lineService.save(line);

        LineRequest secondLine = new LineRequest("8호선", "red", firstStation.getId(), secondStation.getId(), 10, 100);
        LineResponse secondNewLine = lineService.save(secondLine);

        assertThatThrownBy(() -> lineService.update(secondNewLine.getId(),
                new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10, 100)))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("해당 지하철 노선이 존재하고 있습니다.");
    }
}
