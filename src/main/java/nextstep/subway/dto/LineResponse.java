package nextstep.subway.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.entity.Line;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getStations().stream()
                        .map(StationResponse::from)
                        .collect(Collectors.toList())
        );
    }
}