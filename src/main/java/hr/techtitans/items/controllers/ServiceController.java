package hr.techtitans.items.controllers;

import hr.techtitans.items.dtos.ServiceDto;
import hr.techtitans.items.services.ItemService;
import hr.techtitans.items.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices(){
        return new ResponseEntity<List<ServiceDto>>(serviceService.allServices(), HttpStatus.OK);
    }
}
