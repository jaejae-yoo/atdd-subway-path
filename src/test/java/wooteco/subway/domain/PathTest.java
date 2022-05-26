package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.exception.ClientException;
import wooteco.subway.service.infra.DijkstraPathStrategy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTest {

    @Test
    @DisplayName("최단 경로를 조회한다.")
    void findShortestPath() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 2L, 3L, 20);
        Section section3 = new Section(3L, 1L, 3L,4L, 20);

        Path path = new DijkstraPathStrategy().findPath(List.of(section1, section2, section3), 1L, 4L);

        assertThat(path.getDistance()).isEqualTo(50);
        assertThat(path.calculateFare(List.of(new Line(1L, "2호선", "green", 0)), 20L)).isEqualTo(2050);
    }

    @Test
    @DisplayName("최단 경로를 조회한다. - 선택지가 여러 개인 경우 최단 거리 선택")
    void findShortestPathFromVarious() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 2L, 3L, 20);
        Section section3 = new Section(3L, 1L, 2L, 3L, 15);

        Path path = new DijkstraPathStrategy().findPath(List.of(section1, section2, section3), 1L, 3L);

        assertThat(path.getDistance()).isEqualTo(25);
        assertThat(path.calculateFare(List.of(new Line(1L, "2호선", "green", 0)),20L)).isEqualTo(1550);
    }

    @ParameterizedTest
    @CsvSource(value = {"9,1250", "10,1350", "12,1350", "15,1350", "16,1450", "50,2050", "51,2150", "58,2150", "59,2250", "66,2250"})
    @DisplayName("지하철 요금 계산 - 지하철 노선 추가 금액 X")
    void calcNoExtraFare2(int distance, int fare) {
        assertThat(new DijkstraPathStrategy().findPath(List.of(new Section(1L, 1L, 2L, distance)), 1L, 2L)
                .calculateFare(List.of(new Line(1L, "2호선", "green", 0)), 20L)).isEqualTo(fare);
    }

    @ParameterizedTest
    @CsvSource(value = {"5,4,3250,1000,2000", "5,5,1450,100,0", "7,5,1650,200,300", "10,5,4350,3000,1000", "20,30,2060,10,0"})
    @DisplayName("지하철 요금 계산 - 지하철 노선 추가 금액 O")
    void calcExtraFare(int distance1, int distance2, int fare, int firstExtra, int secondExtra) {
        assertThat(new DijkstraPathStrategy().findPath(List.of(new Section(1L, 1L, 2L, distance1),
                        new Section(2L, 2L, 3L, distance2)), 1L, 3L)
                .calculateFare(List.of(new Line(1L, "2호선", "green", firstExtra),
                                new Line(2L, "3호선", "green", secondExtra)), 20L)).isEqualTo(fare);
    }

    @ParameterizedTest
    @CsvSource(value = {"10,500,6", "10,800,13", "9,450,12", "12,800,13", "16,880,18", "50,2050,19", "59,0,0"})
    @DisplayName("지하철 요금 계산 - 지하철 연령병 요금 할인")
    void calcAgeFare(int distance, int fare, Long age) {
        assertThat(new DijkstraPathStrategy().findPath(List.of(new Section(1L, 1L, 2L, distance)), 1L, 2L)
                .calculateFare(List.of(new Line(1L, "2호선", "green", 0)), age)).isEqualTo(fare);
    }

    @Test
    @DisplayName("갈 수 없는 경로 예외")
    void impossiblePath() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 3L, 4L, 20);

        assertThatThrownBy(() -> new DijkstraPathStrategy().findPath(List.of(section1, section2), 1L, 4L))
                .isInstanceOf(ClientException.class)
                        .hasMessageContaining("갈 수 없는 경로입니다.");
    }

    @Test
    @DisplayName("구간에 등록되지 않은 역 예외")
    void notExistInSection() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 3L, 4L, 20);

        assertThatThrownBy(() -> new DijkstraPathStrategy().findPath(List.of(section1, section2), 1L, 5L))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("구간에 등록되지 않은 역입니다.");
    }
}
