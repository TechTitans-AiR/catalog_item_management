package hr.techtitans.items.dtos;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import hr.techtitans.items.models.ItemCategories;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCategoryDto {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    private String name;
}
