package nextstep.subway.unit;

import nextstep.marker.ClassicUnitTest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.vo.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.mockito.Mockito.spy;

@ClassicUnitTest
public class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private PathFinder pathFinder;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        교대역 = getStation(1L, "교대역");
        강남역 = getStation(2L, "강남역");
        양재역 = getStation(3L, "양재역");
        남부터미널역 = getStation(4L, "남부터미널역");

        이호선 = getLine(1L, "2호선", "green", 교대역, 강남역, 10L);
        신분당선 = getLine(2L, "신분당선", "red", 강남역, 양재역, 10L);
        삼호선 = getLine(3L, "3호선", "orange", 교대역, 남부터미널역, 2L);

        createSection(1L, 삼호선, 남부터미널역, 양재역, 3L);


        List<Section> sections = List.of(이호선, 신분당선, 삼호선).stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        pathFinder = new PathFinder(sections);
    }

    @Nested
    class Success {

        @Test
        void 교대역에서_양재역을_가는_최단_경로는_교대_남부터미널_양재_5미터이다() {
            // when
            Path shortestPath = pathFinder.getShortestPath(교대역, 양재역);

            // then
            verifyShortestPath(shortestPath, 5L, "교대역", "남부터미널역", "양재역");
        }

        @Test
        void 강남역에서_남부터미널역을_가는_최단_경로는_강남_교대_남부터미널_12미터이다() {
            // when
            Path shortestPath = pathFinder.getShortestPath(강남역, 남부터미널역);

            // then
            verifyShortestPath(shortestPath, 12L, "강남역", "교대역", "남부터미널역");
        }

        @Test
        void 강남역에서_양재역을_가는_최단_경로는_강남_양재_10미터이다() {
            // when
            Path shortestPath = pathFinder.getShortestPath(강남역, 양재역);

            // then
            verifyShortestPath(shortestPath, 10L, "강남역", "양재역");
        }

        @Test
        void 유효한_경로() {
            // when & then
            Assertions.assertTrue(pathFinder.isValidPath(강남역, 양재역));
        }

        private void verifyShortestPath(Path shortestPath, long distance, String... stationNames) {
            Assertions.assertEquals(distance, shortestPath.getDistance());
            assertThat(shortestPath.getStations()).hasSize(stationNames.length)
                    .map(Station::getName)
                    .containsExactly(stationNames);
        }

    }

    @Nested
    class Fail {

        @Test
        void 출발역과_도착역이_같은_경우() {
            // when & then
            thenCode(() -> pathFinder.getShortestPath(강남역, 강남역)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 출발역과_도착역이_연결되지_않은_경우() {
            // given
            Station 다른역 = getStation(5L,"다른역");

            // when & then
            thenCode(() -> pathFinder.getShortestPath(강남역, 다른역)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 유효하지_않은__경로() {
            // when & then
            Assertions.assertFalse(pathFinder.isValidPath(강남역, 강남역));
        }
    }

    private Line getLine(long id, String name, String color, Station upStation, Station downStation, Long distance) {
        Line line = spy(Line.builder()
                .name(name)
                .color(color)
                .distance(distance)
                .upStation(upStation)
                .downStation(downStation)
                .build());
        BDDMockito.given(line.getId()).willReturn(id);
        return line;

    }

    private Station getStation(long id, String name) {
        Station station = spy(Station.create(() -> name));
        BDDMockito.given(station.getId()).willReturn(id);
        return station;
    }

    private void createSection(long id, Line line, Station upStation, Station downStation, Long distance) {
        Section section = spy(Section.of(upStation, downStation, distance));
        BDDMockito.given(section.getId()).willReturn(id);
        line.add(section);
    }
}