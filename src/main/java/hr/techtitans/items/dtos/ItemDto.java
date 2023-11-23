package hr.techtitans.items.dtos;
import hr.techtitans.items.models.ItemCategories;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String id;

    private ItemCategories itemCategory;

    private String name;

    private String description;

    private Double price;

    private Integer quantity_in_stock;

    private Double weight;

    private String material;

    private String brand;

    private String currency;
}
