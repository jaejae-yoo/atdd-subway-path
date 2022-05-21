package wooteco.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Path findShortestPath(Long source, Long target, Long age) {
        WeightedMultigraph<Long, SectionWeightEdge> graph = new WeightedMultigraph(SectionWeightEdge.class);
        initPathGraph(graph, gatherStationIds());
        GraphPath path = new DijkstraShortestPath(graph).getPath(source, target);
        return new Path(path.getEdgeList(), (int) path.getWeight(), age);
    }

    private Set<Long> gatherStationIds() {
        Set<Long> ids = new HashSet<>();
        for (Section section : sections) {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        }
        return ids;
    }

    private void initPathGraph(WeightedMultigraph<Long, SectionWeightEdge> graph, Set<Long> ids) {
        for (Long id : ids) {
            graph.addVertex(id);
        }

        for (Section section : sections) {
            graph.addEdge(section.getUpStationId(), section.getDownStationId(),
                    new SectionWeightEdge(section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance()));
        }
    }
}
