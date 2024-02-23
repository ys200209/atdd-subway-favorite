package nextstep.subway.unit.service;

import nextstep.common.Constant;
import nextstep.subway.application.LineService;
import nextstep.subway.application.StationService;
import nextstep.subway.application.dto.ShowLineSectionDto;
import nextstep.subway.application.request.AddSectionRequest;
import nextstep.subway.application.response.ShowLineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private final Station 논현역 = Station.from(Constant.논현역);
    private final Long 논현역_ID = 1L;
    private final Station 신논현역 = Station.from(Constant.신논현역);
    private final Long 신논현역_ID = 2L;
    private final Station 강남역 = Station.from(Constant.강남역);
    private final Long 강남역_ID = 3L;
    private final Station 양재역 = Station.from(Constant.양재역);
    private final Long 양재역_ID = 4L;
    private final Section 논현역_신논현역_구간 = Section.of(Station.from(Constant.논현역), Station.from(Constant.신논현역), Constant.역_간격_10);
    private final Section 신논현역_강남역_구간 = Section.of(Station.from(Constant.신논현역), Station.from(Constant.강남역), Constant.역_간격_10);
    private final Section 강남역_양재역_구간 = Section.of(Station.from(Constant.강남역), Station.from(Constant.양재역), Constant.역_간격_10);
    private final Section 논현역_강남역_구간 = Section.of(Station.from(Constant.논현역), Station.from(Constant.강남역), Constant.역_간격_20);
    private final Section 신논현역_양재역_구간 = Section.of(Station.from(Constant.신논현역), Station.from(Constant.양재역), Constant.역_간격_20);
    private final Line 신분당선 = Line.of(Constant.신분당선, Constant.빨간색);
    private final Long 신분당선_ID = 1L;

    @DisplayName("노선 마지막에 구간을 추가한다.")
    @Test
    void 노선_마지막에_구간_등록() {
        // given
        when(stationService.findById(논현역_ID)).thenReturn(논현역);
        when(stationService.findById(신논현역_ID)).thenReturn(신논현역);
        when(stationService.findById(강남역_ID)).thenReturn(강남역);
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10));
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        List<ShowLineSectionDto> 구간들 = 신분당선_조회_응답.getSections();
        구간_변화_검증(구간들, 2, List.of(ShowLineSectionDto.from(논현역_신논현역_구간), ShowLineSectionDto.from(신논현역_강남역_구간)), Constant.역_간격_20);
    }

    @DisplayName("노선 중간에 구간을 추가한다.")
    @Test
    void 노선_중간에_구간_등록() {
        // given
        when(stationService.findById(논현역_ID)).thenReturn(논현역);
        when(stationService.findById(신논현역_ID)).thenReturn(신논현역);
        when(stationService.findById(강남역_ID)).thenReturn(강남역);
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 강남역_ID, Constant.역_간격_20));
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10));

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        List<ShowLineSectionDto> 구간들 = 신분당선_조회_응답.getSections();
        구간_변화_검증(구간들, 2, List.of(ShowLineSectionDto.from(논현역_신논현역_구간), ShowLineSectionDto.from(신논현역_강남역_구간)), Constant.역_간격_20);
    }

    @DisplayName("노선 처음에 구간을 추가한다.")
    @Test
    void 노선_처음에_구간_등록() {
        // given
        when(stationService.findById(논현역_ID)).thenReturn(논현역);
        when(stationService.findById(신논현역_ID)).thenReturn(신논현역);
        when(stationService.findById(강남역_ID)).thenReturn(강남역);
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10));

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        List<ShowLineSectionDto> 구간들 = 신분당선_조회_응답.getSections();
        구간_변화_검증(구간들, 2, List.of(ShowLineSectionDto.from(논현역_신논현역_구간), ShowLineSectionDto.from(신논현역_강남역_구간)), Constant.역_간격_20);
    }

    @DisplayName("노선 마지막 구간을 제거한다.")
    @Test
    void 노선_마지막_구간_제거() {
        // given
        when(stationService.findById(논현역_ID)).thenReturn(논현역);
        when(stationService.findById(신논현역_ID)).thenReturn(신논현역);
        when(stationService.findById(강남역_ID)).thenReturn(강남역);
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10));
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));
        lineService.deleteSection(신분당선_ID, 강남역_ID);

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        List<ShowLineSectionDto> 구간들 = 신분당선_조회_응답.getSections();
        구간_변화_검증(구간들, 1, List.of(ShowLineSectionDto.from(논현역_신논현역_구간)), Constant.역_간격_10);
    }

    @DisplayName("노선 중간 구간을 제거한다.")
    @Test
    void 노선_중간_구간_제거() {
        // given
        when(stationService.findById(논현역_ID)).thenReturn(논현역);
        when(stationService.findById(신논현역_ID)).thenReturn(신논현역);
        when(stationService.findById(강남역_ID)).thenReturn(강남역);
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10));
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));
        lineService.deleteSection(신분당선_ID, 신논현역_ID);

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        List<ShowLineSectionDto> 구간들 = 신분당선_조회_응답.getSections();
        구간_변화_검증(구간들, 1, List.of(ShowLineSectionDto.from(논현역_강남역_구간)), Constant.역_간격_20);
    }

    @DisplayName("노선 처음 구간을 제거한다.")
    @Test
    void 노선_처음_구간_제거() {
        // given
        when(stationService.findById(논현역_ID)).thenReturn(논현역);
        when(stationService.findById(신논현역_ID)).thenReturn(신논현역);
        when(stationService.findById(강남역_ID)).thenReturn(강남역);
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선_ID, AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10));
        lineService.addSection(신분당선_ID, AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10));
        lineService.deleteSection(신분당선_ID, 논현역_ID);

        // then
        ShowLineResponse 신분당선_조회_응답 = lineService.findLine(신분당선_ID);
        List<ShowLineSectionDto> 구간들 = 신분당선_조회_응답.getSections();
        구간_변화_검증(구간들, 1, List.of(ShowLineSectionDto.from(신논현역_강남역_구간)), Constant.역_간격_10);
    }

    void 구간_변화_검증(List<ShowLineSectionDto> 구간들, int 구간_수, List<ShowLineSectionDto> 비교_구간들, int 노선_길이) {
        assertThat(구간들).hasSize(구간_수);
        assertThat(구간들).isEqualTo(비교_구간들);
        assertThat(구간들.stream().mapToInt(ShowLineSectionDto::getDistance).sum()).isEqualTo(노선_길이);
    }

}