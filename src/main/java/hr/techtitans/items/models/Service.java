package hr.techtitans.items.models;

import hr.techtitans.items.repositories.ItemCategoriesRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Document(collection="services")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {
    @Id
    private String id;

    private String serviceName;

    private String description;

    private String serviceProvider;

    private Double price;

    private String currency;

    private Integer duration;

    private String durationUnit;

    private String availability;

    private String serviceLocation;




}
