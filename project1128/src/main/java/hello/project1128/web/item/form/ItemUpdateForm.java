package hello.project1128.web.item.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;


@Data
public class ItemUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 100, max = 1000000, message = "가격은 100원에서 1,000,000원 까지 가능합니다.")
    private Integer price;

    //수정에서는 수량은 자유롭게 변경할 수 있다.
    @Max(value = 9999, message = "최대수량은 9,999개 입니다.")
    private Integer quantity;

    @NotNull
    private String description;

}