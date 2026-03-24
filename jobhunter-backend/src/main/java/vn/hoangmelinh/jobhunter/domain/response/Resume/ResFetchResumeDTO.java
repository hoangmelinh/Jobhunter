package vn.hoangmelinh.jobhunter.domain.response.Resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResFetchResumeDTO {
    private long id;
    private String email;
    private String url;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private String companyName;
    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResume {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobResume {
        private Long id;
        private String name;
    }
}
