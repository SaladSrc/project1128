package hello.project1128.domain.order;

import hello.project1128.domain.Delivery;
import hello.project1128.domain.DeliveryStatus;
import hello.project1128.domain.OrderStatus;
import hello.project1128.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@Table(name = "orders") //클래스 명과 테이블 명이 다를 때 이렇게 명시적으로 어노테이션으로 정의함
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // new Order(); 못하게 됨 직접생성 안됨 (생성메서드 따로 있음)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id") // DB의 컬럼 명칭을 써준다.
    private Long id;

    //테이블에는 member라는 컬럼이 없음 @ManyToOne가 붙으면 테이블간 관계를 정의
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") //@JoinColumn은
    private Member member; //Member 테이블의 member_id를 참고하는 FK이다. (Join이 참고한다는 뜻인듯)

    //OrderItem 클래스의 order 필드가 포린키       //cas~All : 주문을 저장할 때 주문 항목들도 함께 저장
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER,CANCEL]

    //==연관관계 메서드==//
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //OrderService 에서 사용하기 위한 메서드
    //==생성메스드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now().withNano(0));
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    //JPA의 장점이 아래처럼 Entity의 데이터들만 바꿔주면 JPA가 변경내역을 감지해서 DB에 업데이트 쿼리를 날려준다.
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
        }

        this.setStatus(OrderStatus.CANCEL); //Order의 상태를 바꿨기 때문에 Order에 업데이트가 날라갈거고
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel(); //모든 오더를 캔슬
        }
    }

    //==조회로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
