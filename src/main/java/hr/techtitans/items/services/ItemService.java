package hr.techtitans.items.services;



import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.repositories.ItemRepository;
import hr.techtitans.items.repositories.ItemCategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private ItemCategoriesRepository itemCategoriesRepository;

    public List<ItemDto> allItems(){
        List<Item> items = itemRepository.findAll();
        return items.stream().map(this::mapToItemDto).collect(Collectors.toList());

    }

    private ItemDto mapToItemDto(Item item){
        ItemCategories itemCategories = itemCategoriesRepository.findById(item.getItemCategory()).orElse(null);
        return new ItemDto(
                item.getId(),
                itemCategories,
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getQuantity_in_stock(),
                item.getWeight(),
                item.getMaterial(),
                item.getBrand(),
                item.getCurrency()
                );
    }
}

