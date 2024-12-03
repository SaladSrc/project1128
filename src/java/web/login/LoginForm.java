package hello.project1128.web.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty(message = "아이디를 입력해주세요")//검증의 대상
    private String loginId;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    private String password;
}
