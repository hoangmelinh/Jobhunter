package vn.hoangmelinh.jobhunter.domain.response.Resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateResumeDTO {

    private Instant updatedAt;
    private String updatedBy;
}
