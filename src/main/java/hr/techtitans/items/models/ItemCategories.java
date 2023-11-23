package hr.techtitans.items.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="item_categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCategories {
    @Id
    private String id;
    private String name;
}
