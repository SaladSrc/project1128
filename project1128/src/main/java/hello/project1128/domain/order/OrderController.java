package hello.project1128.domain.order;


import hello.project1128.domain.OrderStatus;
import hello.project1128.domain.item.Item;
import hello.project1128.domain.member.Member;
import hello.project1128.web.item.ItemService;
import hello.project1128.web.member.MemberService;
import hello.project1128.web.order.OrderSearch;
import hello.project1128.web.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems(); //모든 아이템 리스트 가져오기

        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("members", members); //멤버 리스트를 model에 넣어버림
        model.addAttribute("items", items); //아이템 리스트를 model에 넣어버림

        return "order/orderForm";
    }

    @PostMapping("/order") // (/order의 return 파일인) orderForm으로 부터 넘어온 데이터들!!!
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {


        orderService.order(memberId, itemId, count); //받은 데이터들을 orderService로 보내버림

        return "redirect:/orders"; //주문을 하면 주문목록을 보여줌
    }

    //주문목록
    @GetMapping("/orders")
    //아래 @ModelAttribute를 통해 orderList의 form의 데이터(이름,주문여부)를 가지고 온다. 그리고 orderSearch 객체에 넣는다.
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, @RequestParam(defaultValue = "0") int page, Model model) {

        List<Order> orders = orderService.findOrders(orderSearch); //그 객체로 주문내역들을 찾는다.
        model.addAttribute("orders", orders); //찾은 주문 내역들을 다시 model에 담아서 form으로 돌려보낸다.

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }


}
