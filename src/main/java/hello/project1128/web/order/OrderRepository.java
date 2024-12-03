package hello.project1128.web.order;

import hello.project1128.domain.order.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository{

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) { //orderSearch에는 이름과 주문여부(order/cancel) 가 들어있다.

        //language=JPAQL
        String jpql = "select o From Order o join o.member m";

        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) { //매게변수로 들어온 orderSearch의 status에 order가 있다면

            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false; //두번째 부터는 where이 아닌 and가 실행될 수 있도록 조치
            }

            jpql += " o.status = :status"; //orderSearch의 getOrderStatus으로 가져온 주문 상태 값이 여기 :status에 들어오게 된다.
        }
        // 즉 이런 쿼리가 완성됨!!!!! --->>>
        // select o From Order o join o.member m where o.status = 'order'

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        // 최종적으로 이런 쿼리가 완성됨!!!!! -->>
        // select o From Order o join o.member m where o.status = :status and m.name like :name


        // Order타입의 타입쿼리를 만듬
        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus()); //위의쿼리 ':status'에 이 값을 넣게됨!!
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName()); //위의쿼리 ':name'에 이 값을 넣게됨!!
        }

        return query.getResultList();
    }


}
