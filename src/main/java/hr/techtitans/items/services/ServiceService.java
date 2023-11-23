package hr.techtitans.items.services;


import hr.techtitans.items.dtos.ServiceDto;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.models.Service;

import hr.techtitans.items.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;



    public List<ServiceDto> allServices(){
        List<Service> services = serviceRepository.findAll();
        return services.stream().map(this::mapToServiceDto).collect(Collectors.toList());

    }

    private ServiceDto mapToServiceDto(Service service) {
        return new ServiceDto(
                service.getId(),
                service.getServiceName(),
                service.getDescription(),
                service.getServiceProvider(),
                service.getPrice(),
                service.getCurrency(),
                service.getDuration(),
                service.getDurationUnit(),
                service.getAvailability(),
                service.getServiceLocation()
        );
    }
}
