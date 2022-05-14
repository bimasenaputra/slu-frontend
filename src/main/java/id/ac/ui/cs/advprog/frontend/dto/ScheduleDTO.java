package id.ac.ui.cs.advprog.frontend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.*;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {
    @NotNull
    private String id;
    @NotNull
    private String title;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    @NotNull
    private String startingLoc;
    @NotNull
    private String destination;

    private String desc;
}
