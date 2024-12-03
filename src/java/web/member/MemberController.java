package hello.project1128.web.member;


import hello.project1128.domain.member.Address;
import hello.project1128.domain.member.Member;
import hello.project1128.domain.member.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
@RequiredArgsConstructor
@RequestMapping
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    //회원가입(get)
    @GetMapping("/members/new")
    public String createForm(Model model) {

        //view로 넘어갈때 이렇게 빈 껍데기라도 가져가는 이유가 validation이라도 해주기 때문에
        model.addAttribute("memberForm", new MemberForm());

        return "members/createMemberForm";

    }

    //회원가입(post)
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        // 로그인 아이디 중복 확인
        if (memberRepository.existsByLoginId(form.getLoginId())) {
            // 2. 중복이 있을 경우 BindingResult에 에러 메시지 추가
            result.rejectValue("loginId", "duplicate.loginId", "이미 사용 중인 아이디입니다.");
        }

        //패스워드 형식 확인
        String password1 = form.getPassword(); //검증전 패스워드
        String password2 = validatePassword(password1, result);

        // 에러가 있으면 회원 가입 폼으로 돌아감
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }




        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setLoginId(form.getLoginId());
        member.setName(form.getName());
        member.setPassword(password2);
        member.setAddress(address);

        memberService.save(member);

        return "redirect:/"; //localhost:8080
    }

    //전체회원 목록
    @GetMapping("/members")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {

        // 페이지 요청이 0보다 작은 값이면 0으로 설정 (예: /members?page=-1로 요청이 왔을 때)
        if (page < 0) {
            page = 0;
        }

        // 페이지 요청에 따라 10개씩 아이템을 가져옵니다.
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이지 처리
        Page<Member> memberPage = memberService.getMembers(pageable);

        if (memberPage == null || !memberPage.hasContent()) {
            model.addAttribute("members", "No members available");
            return "members/memberList"; // 데이터가 없을 경우
        }

        // 페이지네이션 정보를 모델에 추가
        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("totalItems", memberPage.getTotalElements());

        return "members/memberList";
    }

    public static String validatePassword(String password, BindingResult result) {

        // 비밀번호가 6자리 이상인지 확인
        if (password.length() < 6) {
            result.rejectValue("password", "password.tooShort", "비밀번호는 최소 6자리 이상이어야 합니다.");
        }

        // 비밀번호가 문자와 숫자 조합인지 확인
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            result.rejectValue("password", "password.invalidCombination", "비밀번호는 문자와 숫자가 포함되어야 합니다.");
        }
        return password;
    }


}
