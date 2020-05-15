package wooteco.subway.admin.service;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.*;
import wooteco.subway.admin.dto.PathResponse;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exception.CanNotCreateGraphException;
import wooteco.subway.admin.exception.LineNotConnectedException;
import wooteco.subway.admin.exception.OverlappedStationException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PathService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse calculatePath(String source, String target, String type) {
        validateOverlappedStation(source, target);

        Lines allLines = new Lines(lineRepository.findAll());

        LineStations lineStations = new LineStations(allLines.getAllLineStation());

        Stations stations = new Stations(stationRepository.findAll());

        Station sourceStation = stations.findStationByName(source);
        Station targetStation = stations.findStationByName(target);

        WeightedMultigraph<Long, DefaultWeightedEdge> graph;

        try {
            graph = initGraph(lineStations, type);
        } catch (IllegalArgumentException e) {
            throw new CanNotCreateGraphException();
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        List<Long> shortestPathIds;
        try {
            shortestPathIds = dijkstraShortestPath.getPath(sourceStation.getId(), targetStation.getId()).getVertexList();
        } catch (NullPointerException e) {
            throw new LineNotConnectedException();
        }

        List<Station> shortestPath = new ArrayList<>();
        int distance = 0;
        int duration = 0;
        Long preStationId = sourceStation.getId();

        for (Long stationId : shortestPathIds) {
            shortestPath.add(stations.findStationById(stationId));
            LineStation lineStation = lineStations.findLineStation(preStationId, stationId);
            distance += lineStation.getDistance();
            duration += lineStation.getDuration();
            preStationId = stationId;
        }

        return new PathResponse(StationResponse.listOf(shortestPath), distance, duration);
    }

    private void validateOverlappedStation(String source, String target) {
        if (source.equals(target)) {
            throw new OverlappedStationException();
        }
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> initGraph(LineStations lineStations, String type) {
        PathSearchType weight = PathSearchType.of(type);
        WeightedMultigraph<Long, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);

        Set<Long> allStationIds = lineStations.getAllLineStationId();

        for (Long stationId : allStationIds) {
            graph.addVertex(stationId);
        }

        for (LineStation lineStation : lineStations.getLineStations()) {
            if (lineStation.getPreStationId() != null) {
                DefaultWeightedEdge edge = graph.addEdge(lineStation.getPreStationId(), lineStation.getStationId());
                if (weight.is("DISTANCE")) {
                    graph.setEdgeWeight(edge, lineStation.getDistance());
                    continue;
                }
                if (weight.is("DURATION")) {
                    graph.setEdgeWeight(edge, lineStation.getDuration());
                }
            }
        }
        return graph;
    }
}
