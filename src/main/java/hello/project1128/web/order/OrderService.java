package hello.project1128.web.order;


import hello.project1128.domain.Delivery;
import hello.project1128.domain.item.Item;
import hello.project1128.domain.item.ItemRepository;
import hello.project1128.domain.member.Member;
import hello.project1128.domain.member.MemberRepository;
import hello.project1128.domain.order.Order;
import hello.project1128.domain.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    //@RequiredArgsConstructor 와 함께 final을 사용하면 의존성 주입을 확실하게 할 수 있다.
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findById(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        //이 한줄만 써도 되는 이유는 Order에 casecade를 해줬기 때문에 OrderItem, Delivery 자동으로 persist가 된다.
        //order만이 OrderItem, Delivery를 참고하고 있기 때문에

        return order.getId();

    }


    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);

    }

}
