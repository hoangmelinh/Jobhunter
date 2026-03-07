package vn.hoangmelinh.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoangmelinh.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUpdateUserDTO {

    private long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant updatedAt;
    private ComanyUser company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComanyUser {
        private long id;
        private String name;
    }

}
