package wooteco.subway.domain;

import wooteco.subway.exception.ClientException;

import java.util.*;

public class Path {

    private static final int BASIC_FARE = 1250;
    private static final int FIRST_SECTION_FULL_FARE = 800;
    private static final int FIRST_SECTION_UNIT = 5;
    private static final int SECOND_SECTION_UNIT = 8;

    private final List<SectionWeightEdge> edges;
    private final int distance;

    public Path(List<SectionWeightEdge> edges, int distance) {
        this.edges = edges;
        this.distance = distance;
    }

    public int calculateFare(List<Line> lines) {
        int lineExtraFare = findMostExpensiveExtraFare(lines);

        if (distance < 10) {
            return lineExtraFare + BASIC_FARE;
        }
        if (distance <= 50) {
            return lineExtraFare + calcAdditionalFare(distance - 10, FIRST_SECTION_UNIT);
        }
        return lineExtraFare + FIRST_SECTION_FULL_FARE + calcAdditionalFare(distance - 50, SECOND_SECTION_UNIT);
    }

    private int findMostExpensiveExtraFare(List<Line> lines) {
        return edges.stream()
                .mapToInt(edges -> findExtraFare(lines, edges.getLineId()))
                .max()
                .orElseThrow(NoSuchElementException::new);
    }

    private int findExtraFare(List<Line> lines, Long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findAny().orElseThrow(() -> new ClientException("존재하지 않은 line id입니다."))
                .getExtraFare();
    }

    private int calcAdditionalFare(int distance, int unit) {
        int fare = 0;
        fare += (distance / unit) * 100;
        if (distance % unit > 0 || (distance / unit == 0)) {
            fare += 100;
        }
        return BASIC_FARE + fare;
    }

    public Set<Long> getStationIds() {
        Set<Long> stations = new LinkedHashSet<>();
        for (SectionWeightEdge edge : edges) {
            stations.add(edge.getUpStationId());
            stations.add(edge.getDownStationId());
        }
        return Collections.unmodifiableSet(stations);
    }

    public int getDistance() {
        return distance;
    }
}
