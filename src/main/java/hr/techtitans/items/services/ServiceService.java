package hr.techtitans.items.services;


import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.dtos.ServiceDto;
import hr.techtitans.items.models.Catalog;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.models.Service;

import hr.techtitans.items.repositories.ServiceRepository;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;


    public ResponseEntity<Object> checkAdminRole(String token) {
        try {
            String role = getRoleFromToken(token);
            if (!Objects.equals(role, "admin")) {
                return new ResponseEntity<>("Only admin users can perform this action", HttpStatus.UNAUTHORIZED);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while checking admin role", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            String[] tokenParts = token.split("\\.");

            if (tokenParts.length != 3) {
                System.out.println("Invalid token format");
                System.out.println(tokenParts.length);
                return null;
            }

            String payload = tokenParts[1];

            byte[] decodedPayload = java.util.Base64.getUrlDecoder().decode(payload);
            String decodedPayloadString = new String(decodedPayload, StandardCharsets.UTF_8);

            JSONObject payloadJson = new JSONObject(decodedPayloadString);

            String role = payloadJson.getString("role");

            System.out.println("Role from Token in getRoleFromToken: " + role);
            System.out.println(payloadJson);

            return role;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<Object> createService(Map<String, Object> payload, String token) {

        ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
        if (adminCheckResult != null) {
            return adminCheckResult;
        }
        String name = (String) payload.get("serviceName");
        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Service name is mandatory", HttpStatus.BAD_REQUEST);
        }


        Service service = new Service();

        service.setServiceName((String) payload.get("serviceName"));
        service.setDescription((String) payload.get("description"));
        service.setServiceProvider((String) payload.get("serviceProvider"));
        service.setPrice((Double) payload.get("price"));
        service.setCurrency((String) payload.get("currency"));
        service.setDuration((Integer) payload.get("duration"));
        service.setDurationUnit((String) payload.get("durationUnit"));
        service.setServiceLocation((String) payload.get("serviceLocation"));
        service.setAvailability((String) payload.get("availability"));


        serviceRepository.insert(service);
        return new ResponseEntity<>("New service created successfully", HttpStatus.CREATED);
    }
    public List<ServiceDto> allServices(String token){
        String role = getRoleFromToken(token);
        if (!Objects.equals(role, "admin")) {
            return Collections.emptyList();
        }
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

    public ServiceDto getServiceById(String serviceId, String token) {
        ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
        if (adminCheckResult != null) {
            System.out.println("Unauthorized: " + adminCheckResult.getBody());
            return null;
        }
        ObjectId objectId = new ObjectId(serviceId);
        Optional<Service> optionalService = serviceRepository.findById(objectId);

        if (optionalService.isPresent()) {
            Service service = optionalService.get();
            return mapToServiceDto(service);
        } else {
            return null;
        }
    }


    public ResponseEntity<Object> deleteServiceById(String serviceId, String token) {
        try {
            ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
            if (adminCheckResult != null) {
                return adminCheckResult;
            }
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

    public ResponseEntity<?> updateService(String serviceId, Map<String, Object> payload, String token) {
        try {
            ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
            if (adminCheckResult != null) {
                return adminCheckResult;
            }
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
                    serviceToUpdate.setPrice((Double) payload.get("price"));
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
