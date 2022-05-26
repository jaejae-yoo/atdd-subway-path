package wooteco.subway.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import wooteco.subway.domain.Station;
import wooteco.subway.dto.PathResponse;
import wooteco.subway.service.PathService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {PathController.class})
class PathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PathService pathService;

    @Test
    @DisplayName("지하철 역 목록 조회")
    void findStation() throws Exception {
        PathResponse pathResponse = new PathResponse(List.of(new Station("강남역")), 10, 10);

        given(pathService.findShortestPath(any())).willReturn(pathResponse);

        mockMvc.perform(get("/paths")
                        .param("source", String.valueOf(1L))
                        .param("target", String.valueOf(2L))
                        .param("age", String.valueOf(10L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("stations[0].id").value(0L))
                .andExpect(jsonPath("stations[0].name").value("강남역"))
                .andExpect(jsonPath("fare").value(10))
                .andExpect(jsonPath("distance").value(10))
                .andDo(print());
    }
}
