package hello.project1128.domain.item;

import hello.project1128.domain.Category;
import hello.project1128.domain.UploadFile;
import hello.project1128.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;


    @Column(name = "item_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String itemName;

    private Integer price;

    private Integer quantity;

    @Column(name = "description", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;

    @Column(name = "member_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String memberName;

    @Embedded  // 내장형 객체로 처리
    private UploadFile attachFile; //attachFile = 첨부파일

    @ElementCollection
    @CollectionTable(name = "item_image_files", joinColumns = @JoinColumn(name = "item_id"))
    private List<UploadFile> imageFiles;

    @ManyToMany(mappedBy = "items") //@ManyToMany는 중간테이블을 찾아간다.
    private List<Category> categories = new ArrayList<>();




    //==비즈니스 로직==//

    //stock 증가
    public void addStock(int quantity) {
        this.quantity += quantity;
    }

    //stock 감소
    public void removeStock(int quantity) {
        int restStock = this.quantity - quantity;
        if (restStock <0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.quantity = restStock;


    }

}