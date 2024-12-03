package hello.project1128.domain.member;

import hello.project1128.domain.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String loginId;

    @NotEmpty
    @Column(name = "name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String name;

    @NotEmpty
    private String password;

    @Embedded
    private Address address; // String city, street, zipcode

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
