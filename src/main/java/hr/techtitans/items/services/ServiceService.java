package hr.techtitans.items.services;


import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.dtos.ServiceDto;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.models.Service;

import hr.techtitans.items.repositories.ServiceRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public ServiceDto getServiceById(String serviceId) {
        ObjectId objectId = new ObjectId(serviceId);
        Optional<Service> optionalService = serviceRepository.findById(objectId);

        if (optionalService.isPresent()) {
            Service service = optionalService.get();
            return mapToServiceDto(service);
        } else {
            return null;
        }
    }


    public ResponseEntity<Object> deleteServiceById(String serviceId) {
        try {
            if (serviceId == null || serviceId.isEmpty()) {
                return new ResponseEntity<>("Service ID not provided", HttpStatus.BAD_REQUEST);
            }
            ObjectId objectId = new ObjectId(serviceId);
            if (serviceRepository.existsById(objectId)) {
                serviceRepository.deleteById(objectId);
                Map<String, Object> responseBody = Map.of("message", "Service deleted successfully");
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                Map<String, Object> responseBody = Map.of("message", "Service not found");
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> responseBody = Map.of("message", "Service not found");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> responseBody = Map.of("message", "An error occurred");
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateService(String serviceId, Map<String, Object> payload) {
        try {
            if (serviceId == null || serviceId.isEmpty()) {
                return new ResponseEntity<>("Service ID not provided", HttpStatus.BAD_REQUEST);
            }
            ObjectId objectId=new ObjectId(serviceId);
            Optional<Service> existingService = serviceRepository.findById(objectId);

            if (existingService.isPresent()) {
                Service serviceToUpdate = existingService.get();
                if(payload.containsKey("serviceName")){
                    serviceToUpdate.setServiceName((String) payload.get("serviceName"));
                }
                if (payload.containsKey("description")) {
                    serviceToUpdate.setDescription((String) payload.get("description"));
                }
                if (payload.containsKey("price")) {
                    serviceToUpdate.setPrice((Integer) payload.get("price"));
                }
                if (payload.containsKey("serviceProvider")) {
                    serviceToUpdate.setServiceProvider((String) payload.get("serviceProvider"));
                }
                if (payload.containsKey("duration")) {
                    serviceToUpdate.setDuration((Integer) payload.get("duration"));
                }
                if (payload.containsKey("availability")) {
                    serviceToUpdate.setAvailability((String) payload.get("availability"));
                }
                if (payload.containsKey("serviceLocation")) {
                    serviceToUpdate.setServiceLocation((String) payload.get("serviceLocation"));
                }
                if (payload.containsKey("currency")) {
                    serviceToUpdate.setCurrency((String) payload.get("currency"));
                }
                if (payload.containsKey("durationUnit")) {
                    serviceToUpdate.setDurationUnit((String) payload.get("durationUnit"));
                }
                serviceRepository.save(serviceToUpdate);
                return new ResponseEntity<>("Service updated successfully", HttpStatus.OK);
            }else {
                return new ResponseEntity<>("Service not found", HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> responseBody = Map.of("message", "Service not found");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> responseBody = Map.of("message", "An error occurred");
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
