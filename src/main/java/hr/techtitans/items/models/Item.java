package hr.techtitans.items.models;

import hr.techtitans.items.repositories.ItemCategoriesRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection="articles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    private String id;

    @Field(name = "category")
    private ObjectId itemCategory;

    private String name;

    private String description;

    private Double price;

    private Integer quantity_in_stock;

    private Double weight;

    private String material;

    private String brand;

    private String currency;


    public ItemCategories getItemCategory(ItemCategoriesRepository itemCategoriesRepository) {
        return itemCategoriesRepository.findById(itemCategory).orElse(null);
    }



}
