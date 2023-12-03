package hr.techtitans.items.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection="catalogs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog {

    @Id
    private ObjectId id;

    private String name;

    private List<ObjectId> article;

    private List<ObjectId> service;

    private List<ObjectId> users;

    private LocalDateTime date_created;

    private LocalDateTime date_modified;
}
