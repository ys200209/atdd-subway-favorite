package nextstep.subway.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.LineRepository;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.addition.SectionAdditionHandlerMapping;
import nextstep.subway.domain.entity.deletion.SectionDeletionHandlerMapping;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.entity.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.ModifyLineRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionAdditionHandlerMapping sectionAdditionHandlerMapping;
    private final SectionDeletionHandlerMapping sectionDeletionHandlerMapping;

    @Transactional
    public LineResponse save(LineRequest request) {
        Line savedLine = lineRepository.save(convertToLine(request));

        return LineResponse.from(savedLine);
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = getLine(id);

        return LineResponse.from(line);
    }

    @Transactional
    public void updateLine(Long id, ModifyLineRequest request) {
        Line line = getLine(id);

        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(long lineId, SectionRequest request) {
        Line line = getLine(lineId);

        Section newSection = getSection(request, line);

        line.addSection(sectionAdditionHandlerMapping, newSection);
    }

    @Transactional
    public void deleteSection(long lineId, long stationId) {
        Station deleteReqStation = getStation(stationId);

        Line line = getLine(lineId);

        line.removeSection(sectionDeletionHandlerMapping, deleteReqStation);
    }

    private Section getSection(SectionRequest request, Line line) {
        Station newSectionUpStation = getStation(request.getUpStationId());
        Station newSectionDownStation = getStation(request.getDownStationId());
        Section newSection = new Section(line, newSectionUpStation, newSectionDownStation, request.getDistance());
        return newSection;
    }

    private Line getLine(long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("line.not.found"));
    }

    private Station  getStation(Long stationId) {
        Station upStation = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException("station.not.found"));
        return upStation;
    }

    private Line convertToLine(LineRequest request) {
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());
        return new Line(request.getName(), request.getColor(), request.getDistance(), upStation, downStation);
    }
}