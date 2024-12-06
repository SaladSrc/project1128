package hello.project1128.web.item;


import hello.project1128.domain.UploadFile;
import hello.project1128.domain.item.Item;
import hello.project1128.domain.item.ItemRepository;
import hello.project1128.domain.member.Member;
import hello.project1128.file.FileStore;
import hello.project1128.web.SessionConst;
import hello.project1128.web.item.form.ItemSaveForm;
import hello.project1128.web.item.form.ItemUpdateForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    //전체목록
    @GetMapping
    public String listItems(@RequestParam(defaultValue = "0") int page, Model model) {

        // 페이지 요청이 0보다 작은 값이면 0으로 설정 (예: /members?page=-1로 요청이 왔을 때)
        if (page < 0) {
            page = 0;
        }

        // 페이지 요청에 따라 10개씩 아이템을 가져옵니다.
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이지 처리
        Page<Item> itemPage = itemService.getItems(pageable);

        if (itemPage == null || !itemPage.hasContent()) {
            model.addAttribute("items", "No items available");
            return "items/itemList"; // 데이터가 없을 경우
        }


        // 페이지네이션 정보를 모델에 추가
        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());

        return "items/itemList"; // 해당 페이지로 뷰 반환
    }


    //선택상품 상세페이지
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {

        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

/*        for (UploadFile imageFile : item.getImageFiles()) {
            System.out.println("대박!!!!!" + imageFile.getStoreFileName());
        }*/

        return "items/item";
    }

    //상품등록
    @GetMapping("/new")
    public String addForm(Model model) {


        Item item = new Item();


        model.addAttribute("item", item); //빈껍데기 Item객체
        return "items/addForm";
    }

    //상품등록
    //적고 저장을 누르면 적은 데이터들이 item 객체로 들어온다.
    @PostMapping("/new")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) throws IOException {

        //특정 필드 예외가 아닌 전체 예외
        if (form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                //reject는 글로벌 오류
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //특정필드 예외 (즉, 개별예외에 발생하면 )
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "items/addForm";
        }

        // form.getAttachFile()을 attachFile로 저장 (일반 첨부파일 처리)
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());

        // form.getAttachFile()을 이미지 파일 목록으로도 처리
        List<UploadFile> storeImageFiles = new ArrayList<>();
        // 이미지 목록에 동일한 파일을 저장하는 방식
        storeImageFiles.add(attachFile);


        //이렇게 하면 안됨
        //UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        //List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getAttachFile());

        log.info("attachFile ={}", attachFile);
        log.info("storeImageFiles ={}", attachFile);

        Item item = new Item();

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (member != null) {

            item.setMemberName(member.getName());
            System.out.println("Member Name!: " + member.getName());  // '정진고' 잘 출력됨
        }

        //성공 로직
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());
        item.setDescription(form.getDescription());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles); //item의 imageFiles에 'List<UploadFile> storeImageFiles' 를 넣음!!!




        Item savedItem = itemService.saveItem(item);

        //redirectAttributes는 redirect할때 {파라미터}에 값을 넣을 수 있게 해준다.
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "items/editForm";
    }

    //이미지 띄워주는 메서드
    //item-view에 있는
    //<img th:each="imageFile : ${item.imageFiles}" th:src="|/images/${imageFile.getStoreFileName()}|" width="300" height="300"/>
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {

        System.out.println("이미지 보여주기 메서드야~~~~~~~~!!!!");

        String fullPath = fileStore.getFullPath(filename);
        System.out.println("Full file path!!!!!!!!!!!!~~~~~`: " + fullPath);

        File file = new File(fullPath);
        if (!file.exists()) {
            System.out.println("파일이 존재하지 않습니다~~~~~~~!!!!!!!!!!: " + fullPath);  // 파일 존재 여부 확인
        }

        //UrlResource라는 객체는 'file:경로'를 해주면 그 경로의 똑같은 이름의 파일과 연결해 준다.
        return new UrlResource( "file:" + fileStore.getFullPath(filename));
    }

    //첨부파일 다운로드
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {

        //고유번호(Id)를 통해 Item을 찾는다.
        Item item = itemRepository.findById(itemId);

        //해당 Item의 첨부파일(UploadFile)의 서버용파일이름(storeFileName)을 얻어낸다.
        String storeFileName = item.getAttachFile().getStoreFileName(); //아무나 다운받는게 아니라 권한이 있는 사람만

        //그 첨부파일(UploadFile)의 실제파일이름(uploadFileName)을 얻어낸다.
        String uploadFileName = item.getAttachFile().getUploadFileName();

        //UrlResource라는 객체는 'file:경로'를 해주면 그 경로의 파일까지 연결해 준다.
        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);


        String encodingUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);


        String contentDisposition = "attachment; filename=\"" + encodingUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) //익
                .body(resource);
    }


    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {

        //특정 필드 예외가 아닌 전체 예외
        if (form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "items/editForm";
        }

        Item itemParam = new Item();
        itemParam.setItemName(form.getItemName());
        itemParam.setPrice(form.getPrice());
        itemParam.setQuantity(form.getQuantity());
        itemParam.setDescription(form.getDescription());

        itemRepository.update(itemId, itemParam);
        return "redirect:/items/{itemId}";
    }

}
