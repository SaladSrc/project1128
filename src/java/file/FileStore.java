package hello.project1128.file;


import hello.project1128.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//파일을 저장하는 클래스

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {

        return fileDir + filename;

    }
    //이미지들 저장
    //여러 파일 저장하는 매서드
    public List<UploadFile> storeFiles(MultipartFile multipartFile) throws IOException {

        List<UploadFile> storeFileResult = new ArrayList<>();

        //for (MultipartFile multipartFile : multipartFiles) {

            //multipartFile이 있다면
            if (!multipartFile.isEmpty()) {
                //UploadFile로 바꾸고
                UploadFile uploadFile = storeFile(multipartFile);
                //그 UploadFile을 storeFileResult에 넣어라
                storeFileResult.add(uploadFile);
            }
        //}
        return storeFileResult;
    }

    // 첨부파일직행
    // 실제파일명, 저장파일명 두개 생성
    // 저장파일명을 실제 경로에 저장
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        //실제 파일명
        String originalFilename = multipartFile.getOriginalFilename();
        //서버에 저장하는 파일명
        String storeFileName = createStoreFileName(originalFilename);

        //해당경로 + storeFileName으로 파일을 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        //UploadFile 객체 반환
        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1); //ext=확장자
    }

}
