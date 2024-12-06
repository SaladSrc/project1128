package hello.project1128.domain.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor //EntitiyManager를 생성자 주입 해줌
public class MemberRepository implements PagingAndSortingRepository<Member, Long> {

    private final EntityManager em;

    //회원가입
    public void save(Member member) {

        em.persist(member);
    }

    public Member findOne(Long id) {

        return em.find(Member.class, id);
    }



    //로그인 창에 기입한 아이디를 가져와서 기존 DB에 아이디가 있는지 확인
    public Optional<Member> findByLoginId(String loginId) {

        // JPQL을 사용하여 로그인 아이디로 회원을 조회
        List<Member> members = em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
                .setParameter("loginId", loginId) // 로그인 아이디를 파라미터로 설정
                .getResultList(); // 결과 리스트를 가져옴

        // 결과가 없으면 Optional.empty() 반환, 있으면 Optional.of()로 감싸서 반환
        return members.stream().findFirst();

    }

    //모든 회원 목록
    public List<Member> findAll() {

        return em.createQuery("select m from Member m", Member.class).getResultList();
    }


    @Override
    public Iterable<Member> findAll(Sort sort) {

        return null;
    }


    @Override
    public Page<Member> findAll(Pageable pageable) {
        // 쿼리 작성: 전체 아이템을 조회하는 쿼리
        String queryStr = "SELECT m FROM Member m";  // 기본적인 JPQL 쿼리

        TypedQuery<Member> query = em.createQuery(queryStr, Member.class);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        // 전체 아이템의 개수를 구하는 쿼리
        String countQueryStr = "SELECT COUNT(m) FROM Member m";
        TypedQuery<Long> countQuery = em.createQuery(countQueryStr, Long.class);
        Long totalCount = countQuery.getSingleResult();

        // 페이징된 결과를 Page 객체로 반환
        return new PageImpl<>(query.getResultList(), pageable, totalCount);
    }

    public boolean existsByLoginId(String loginId) {
        String jpql = "SELECT COUNT(m) FROM Member m WHERE m.loginId = :loginId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("loginId", loginId)
                .getSingleResult();
        return count > 0;  // 1 이상이면 존재, 0이면 없음
    }





}
