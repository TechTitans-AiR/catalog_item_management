package hr.techtitans.items.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDto {
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
