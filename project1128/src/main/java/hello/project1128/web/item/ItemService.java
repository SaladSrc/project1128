package hello.project1128.web.item;

import hello.project1128.domain.item.Item;
import hello.project1128.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Item saveItem(Item item) {

        Item savedItem = itemRepository.save(item);

        return savedItem;

    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findById(itemId);
        findItem.setItemName(name);
        findItem.setPrice(price);
        findItem.setQuantity(stockQuantity);
    }


    public List<Item> findItems() {
        return itemRepository.findAll();
    }


    public Item findOne(Long itemId) {
        return itemRepository.findById(itemId);
    }


    public Page<Item> getItems(Pageable pageable) {
        Page<Item> page = itemRepository.findAll(pageable);  // findAll(Pageable pageable)을 호출하여 Page 반환
        if (page == null) {
            throw new IllegalStateException("Pageable request returned null");
        }
        return page;
    }



}
