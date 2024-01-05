package hr.techtitans.items.controllers;

import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.dtos.ServiceDto;
import hr.techtitans.items.services.ItemService;
import hr.techtitans.items.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices(){
        return new ResponseEntity<List<ServiceDto>>(serviceService.allServices(), HttpStatus.OK);
    }
    @PutMapping("/update/{serviceId}")
    public ResponseEntity<?> updateService(@PathVariable String serviceId, @RequestBody Map<String, Object> payload) {
        try {
            ResponseEntity<?> response = serviceService.updateService(serviceId, payload);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Service updated successfully.");
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found with id: " + serviceId);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating the service: " + response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating the service: " + e.getMessage());
        }
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<?> getServiceById(@PathVariable String serviceId){
        try {
            ServiceDto serviceDto = serviceService.getServiceById(serviceId);

            if (serviceDto != null) {
                return new ResponseEntity<>(serviceDto, HttpStatus.OK);
            } else {
                String errorMessage = "Service with id: " + serviceId + " is not found.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{serviceId}")
    public ResponseEntity<Object> deleteService(@PathVariable String serviceId) {
        return serviceService.deleteServiceById(serviceId);
    }
    @DeleteMapping("/delete/")
    public ResponseEntity<Object> noServiceIdProvided() {
        Map<String, Object> responseBody = Map.of("message", "Please provide an service ID");
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}