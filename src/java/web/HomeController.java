package hello.project1128.web;


import hello.project1128.domain.member.Member;
import hello.project1128.domain.member.MemberRepository;
import hello.project1128.web.argumentresolver.Login;
import hello.project1128.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;




    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {
    //public String homeLogin(@CookieValue(name ="memberId", required =false) Long memberId, Model model) {


        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        //세션이 있으면 여기로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}