package hello.project1128.web.member;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "아이디는 필수 입니다.")
    private String loginId;

    @NotEmpty(message = "비밀번호는 필수 입니다.")
    private String password;

    @NotEmpty(message = "이름은 필수 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;

}
