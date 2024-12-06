package hello.project1128.web.member;


import hello.project1128.domain.member.Member;
import hello.project1128.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;

//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    } @RequiredArgsConstructor가 이 코드 자동 적용해줌

    //회원가입
    @Transactional //예는 readOnly = false (디폴트) 가 적용됨
    public Long save(Member member) {

        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId(); //디비에 값이 들어가지 않아도 펄시스트컨택스트에서 이미 생성되기 때문에 값이 있다는게 보장됨

    }

    public void validateDuplicateMember(Member member) {
        Optional<Member> findMembers = memberRepository.findByLoginId(member.getLoginId());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

    }



    //회원전체조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //회원한명조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional//springframework의 @Transacional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }


    public Page<Member> getMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);  // findAll(Pageable pageable)을 호출하여 Page 반환
        if (page == null) {
            throw new IllegalStateException("Pageable request returned null");
        }
        return page;
    }
}
