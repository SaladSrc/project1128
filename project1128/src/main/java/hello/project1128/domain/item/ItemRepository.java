package hello.project1128.domain.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository implements PagingAndSortingRepository<Item, Long> {

    private final EntityManager em;

    public ItemRepository(EntityManager em) {
        this.em = em;
    }


    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    public Item findById(Long id) {

        return em.find(Item.class, id);
    }

    public List<Item> findAll() {

        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        findItem.setDescription(updateParam.getDescription());
    }


    @Override
    public Iterable<Item> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Item> findAll(Pageable pageable) {
        // 쿼리 작성: 전체 아이템을 조회하는 쿼리
        String queryStr = "SELECT i FROM Item i";  // 기본적인 JPQL 쿼리

        TypedQuery<Item> query = em.createQuery(queryStr, Item.class);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        // 전체 아이템의 개수를 구하는 쿼리
        String countQueryStr = "SELECT COUNT(i) FROM Item i";
        TypedQuery<Long> countQuery = em.createQuery(countQueryStr, Long.class);
        Long totalCount = countQuery.getSingleResult();

        // 페이징된 결과를 Page 객체로 반환
        return new PageImpl<>(query.getResultList(), pageable, totalCount);
    }


}
