package wooteco.subway.acceptance.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.LineJdbcDao;
import wooteco.subway.domain.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class LineJdbcDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineJdbcDao(jdbcTemplate);
        List<Object[]> splitLine = new ArrayList<>(Arrays.asList(new String[]{"신분당선", "green"},
                new String[]{"3호선", "black"}, new String[]{"1호선", "red"}));
        jdbcTemplate.batchUpdate("INSERT INTO line (name, color) VALUES (?, ?)", splitLine);
    }

    @DisplayName("노선정보 저장")
    @Test
    void save() {
        Line line = lineDao.save(new Line("분당선", "bg-red-600", 900));

        assertThat(line.getName()).isEqualTo("분당선");
    }

    @DisplayName("노선 정보 전체 조회")
    @Test
    void findAll() {
        assertThat(lineDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("노선 정보 삭제")
    @Test
    void delete() {
        Line lineResponse = lineDao.save(new Line("4호선", "blue", 900));

        assertThat(lineDao.delete(lineResponse.getId())).isOne();
    }

    @DisplayName("노선 정보 조회")
    @Test
    void find() {
        Line lineResponse = lineDao.save(new Line("5호선", "blue", 900));

        assertThat(lineDao.findById(lineResponse.getId()).getName()).isEqualTo("5호선");
    }

    @DisplayName("노선 정보 변경")
    @Test
    void update() {
        Line lineResponse = lineDao.save(new Line("7호선", "blue", 900));

        assertThat(lineDao.update(lineResponse.getId(), new Line("7호선", "red", 900))).isOne();
    }
}
