package hr.techtitans.items.services;

import hr.techtitans.items.dtos.ItemCategoryDto;
import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.repositories.ItemCategoriesRepository;
import hr.techtitans.items.repositories.ItemRepository;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemCategoryService {
    @Autowired
    private ItemCategoriesRepository itemCategoriesRepository;

    public List<ItemCategoryDto> getAllCategories(){
        List<ItemCategories> categories = itemCategoriesRepository.findAll();
        List<ItemCategoryDto> categoryDtos = categories.stream()
                .filter(category -> category.getName() != null)
                .map(this::mapToItemCategoryDto)
                .collect(Collectors.toList());
        return categoryDtos;
    }
    private ItemCategoryDto mapToItemCategoryDto(ItemCategories category) {
        ItemCategoryDto itemCategoryDto = new ItemCategoryDto();
        itemCategoryDto.setId(category.getId());
        itemCategoryDto.setName(category.getName());
        return itemCategoryDto;
    }
}
