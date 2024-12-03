package hello.project1128.domain;

import hello.project1128.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany //@ManyToMany가 적힌 변수는 중간테이블로 간다.
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = LAZY) //이 클래스가 여러개, parent 한개
    @JoinColumn(name = "parent_id")
    private Category parent; //이 코드로 인해 JPA가 부모 테이블을 만든다.

    @OneToMany(mappedBy = "parent") //이 클래스가 한개, child 여러개
    private List<Category> child = new ArrayList<>(); //이 코드로 인해 JPA가 자식 테이블을 만든다.

    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child); //위의 List<Category> child를 의미하는 듯
        child.setParent(this);
    }
}
