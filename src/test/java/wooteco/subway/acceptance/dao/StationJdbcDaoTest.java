package wooteco.subway.acceptance.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Station;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class StationJdbcDaoTest {

    private StationDao stationJdbcDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationJdbcDao = new StationJdbcDao(jdbcTemplate);

        List<Object[]> splitStation = Arrays.asList("선릉역", "잠실역", "강남역").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO station(name) VALUES (?)", splitStation);
    }

    @DisplayName("역 정보 저장")
    @Test
    void save() {
        Station newStation = stationJdbcDao.save(new Station("역삼역"));

        assertThat(newStation.getName()).isEqualTo("역삼역");
    }

    @DisplayName("역 정보 전체 조회")
    @Test
    void findAll() {
        assertThat(stationJdbcDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("역 정보 삭제")
    @Test
    void delete() {
        Station newStation = stationJdbcDao.save(new Station("역삼역"));

        assertThat(stationJdbcDao.delete(newStation.getId())).isEqualTo(1);
    }
}
