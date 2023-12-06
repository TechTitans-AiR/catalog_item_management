package hr.techtitans.items.dtos;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import hr.techtitans.items.models.Catalog;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CatalogDto {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> articles;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> services;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> users;

    private LocalDateTime date_created;

    private LocalDateTime date_modified;
}
