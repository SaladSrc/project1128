package hello.project1128.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

//파일이름


@Data
@Embeddable
public class UploadFile {

    @Column(name = "upload_file_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String uploadFileName; //실제 파일 이름

    private String storeFileName; //가공된 파일 이름


    // 기본 생성자 추가(@Embeddable로 선언된 클래스는 JPA가 객체를 자동으로 생성할 때 기본 생성자가 필요)
    public UploadFile() {
    }

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
