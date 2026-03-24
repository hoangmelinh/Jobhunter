package vn.hoangmelinh.jobhunter.domain.response.Job;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoangmelinh.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;

    private String title;

    private String location;

    private LevelEnum level;

    private String description;

    private double salary;

    private int quantity;

    private Instant startDate;

    private Instant endDate;

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    private List<String> skills;

}
