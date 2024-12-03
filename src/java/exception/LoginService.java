package hello.project1128.domain.login;

import hello.project1128.domain.member.Member;
import hello.project1128.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /*
    *  @return null이면 로그인 실패
    * */

    //Reposi에서 아이디를 찾아온다면 그 회원의 비밀번호와 내가 적은 비밀번호가 있는지 확인
    @Transactional
    public Member login(String loginId, String password) {

/*        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);
        Member member = findMemberOptional.get();
        if (member.getPassword().equals(password)) {
            return member;
        } else {
            return null;
        }
        위 코드가 아래 두줄로 요약됨 */

        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);

    }
}
