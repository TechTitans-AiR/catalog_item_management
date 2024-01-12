package hr.techtitans.items.services;

import hr.techtitans.items.dtos.CatalogDto;
import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.models.Catalog;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.repositories.CatalogRepository;
import hr.techtitans.items.repositories.ItemRepository;
import hr.techtitans.items.repositories.ServiceRepository;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CatalogService {
    @Value("${userApiUrl}")
    private String userApiUrl;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private ItemRepository itemRepository;

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

    public String getUserIdFromToken(String token) {
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

            String userIdFromToken = payloadJson.getString("userId");

            System.out.println("userIdFromToken from Token: " + userIdFromToken);
            System.out.println(payloadJson);

            return userIdFromToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<Object> checkUserAccess(String token, String catalogId) {
        try {
            String userId = getUserIdFromToken(token);
            Optional<Catalog> optionalCatalog = catalogRepository.findById(new ObjectId(catalogId));

            if (optionalCatalog.isPresent()) {
                Catalog catalog = optionalCatalog.get();
                List<String> catalogUserIds = catalog.getUsers().stream()
                        .map(ObjectId::toString)
                        .collect(Collectors.toList());

                if (catalogUserIds.contains(userId) || isMerchant(token) || isAdmin(token)) {
                    return null;
                } else {
                    return new ResponseEntity<>("User does not have access to this catalog", HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>("Catalog not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while checking user access", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isMerchant(String token) {
        String role = getRoleFromToken(token);
        return Objects.equals(role, "merchant");
    }

    private boolean isAdmin(String token) {
        String role = getRoleFromToken(token);
        return Objects.equals(role, "admin");
    }

    public ResponseEntity<Object> createCatalog(Map<String, Object> payload, String token) {

        ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
        if (adminCheckResult != null) {
            return adminCheckResult;
        }
        String name = (String) payload.get("name");
        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Name is mandatory", HttpStatus.BAD_REQUEST);
        }


        List<ObjectId> articleIds = (List<ObjectId>) payload.get("articles");
        List<ObjectId> serviceIds = (List<ObjectId>) payload.get("services");
        if ((articleIds == null || articleIds.isEmpty()) && (serviceIds == null || serviceIds.isEmpty())) {
            return new ResponseEntity<>("At least one article or service is required", HttpStatus.BAD_REQUEST);
        }


        Catalog catalog = new Catalog();
        List<ObjectId> userIds = (List<ObjectId>) payload.get("users");
        LocalDateTime currentDateTime = LocalDateTime.now();

        catalog.setName(name);
        catalog.setArticles(articleIds);
        catalog.setServices(serviceIds);
        catalog.setUsers(userIds);
        catalog.setDate_created(currentDateTime);
        catalog.setDate_modified(currentDateTime);
        catalog.setDisabled(false);


        catalogRepository.insert(catalog);
        return new ResponseEntity<>("Catalog created successfully", HttpStatus.CREATED);
    }

    public List<CatalogDto> allCatalogs(String token){
        try {
            String role = getRoleFromToken(token);
            if (!Objects.equals(role, "admin")) {
                return Collections.emptyList();
            }
            List<Catalog> catalogs = catalogRepository.findAll();
            return catalogs.stream().map(this::mapToCatalogDto).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error in allCatalogs: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private CatalogDto mapToCatalogDto(Catalog catalog){
        return new CatalogDto(
                catalog.getId(),
                catalog.getName(),
                catalog.getArticles(),
                catalog.getServices(),
                catalog.getUsers(),
                catalog.getDate_created(),
                catalog.getDate_modified(),
                catalog.getDisabled()
        );
    }
    private List<CatalogDto> mapToCatalogDtoList(List<Catalog> catalogs) {
        List<CatalogDto> catalogDtos = new ArrayList<>();
        for (Catalog catalog : catalogs) {

            if (catalog.getUsers() != null && !catalog.getUsers().isEmpty()) {
                catalogDtos.add(mapToCatalogDto(catalog));
            } else {

                System.out.println("Warning: Catalog with ID " + catalog.getId() + " does not have 'users' property.");
            }
        }
        return catalogDtos;
    }

    public CatalogDto getCatalogById(String catalogId, String token) {
        ResponseEntity<Object> userAccessCheckResult = checkUserAccess(token, catalogId);
        if (userAccessCheckResult != null) {
            System.out.println("Unauthorized: " + userAccessCheckResult.getBody());
            return null;
        }

        ObjectId objectId = new ObjectId(catalogId);
        Optional<Catalog> optionalCatalog = catalogRepository.findById(objectId);

        if (optionalCatalog.isPresent()) {
            Catalog catalog = optionalCatalog.get();
            return mapToCatalogDto(catalog);
        } else {
            return null;
        }
    }

    public List<CatalogDto> searchCatalogs(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            List<Catalog> allCatalogs = catalogRepository.findAll();
            return mapToCatalogDtoList(allCatalogs);
        }

        List<Catalog> searchResults = null;

        for (String key : payload.keySet()) {
            List<Catalog> currentResults = new ArrayList<>();

            switch (key) {
                case "name":
                    String catalogName = (String) payload.get("name");
                    currentResults = catalogRepository.findByNameContainingIgnoreCase(catalogName);
                    break;
                case "article":
                    ObjectId articleId = new ObjectId((String) payload.get("article"));
                    currentResults = catalogRepository.findByArticlesContaining(articleId);
                    break;
                case "service":
                    ObjectId serviceId = new ObjectId((String) payload.get("service"));
                    currentResults = catalogRepository.findByServicesContaining(serviceId);
                    break;
                case "user":
                    ObjectId userId = new ObjectId((String) payload.get("user"));
                    currentResults = catalogRepository.findByUsersContains(userId);
                    break;
                default:
                    break;
            }

            if (searchResults == null) {
                searchResults = currentResults;
            } else {
                searchResults.retainAll(currentResults);
            }
        }

        if (searchResults == null) {
            return Collections.emptyList();
        }

        List<CatalogDto> catalogsDto = mapToCatalogDtoList(searchResults);
        return catalogsDto;
    }


    public class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }


    public List<CatalogDto> getCatalogsByUserId(String userId, String token) {
        try {
            if (checkUserAccess1(token, userId)) {
                ObjectId objectId = new ObjectId(userId);
                List<Catalog> catalogs = catalogRepository.findByUsersContains(objectId);

                if (catalogs != null) {
                    return mapToCatalogDtoList(catalogs);
                } else {
                    throw new NotFoundException("Catalogs not found for user with ID: " + userId);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have access to this operation");
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid user ID: " + userId);
        }
    }

    public boolean checkUserAccess1(String token, String userId) {
        try {
            String loggedInUserId = getUserIdFromToken(token);
            if (isMerchant(token)) {
                return Objects.equals(loggedInUserId, userId);
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean disableCatalog(String catalogId, String token){
        ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
        if (adminCheckResult != null) {

            System.out.println("Unauthorized: " + adminCheckResult.getBody());
            return false;
        }
        ObjectId objectId = new ObjectId(catalogId);
        Optional<Catalog> optionalCatalog=catalogRepository.findById(objectId);
        if (optionalCatalog.isPresent()) {
            Catalog catalog = optionalCatalog.get();

            if (!catalog.getDisabled()) {
                catalog.setDisabled(true);
                catalogRepository.save(catalog);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean enableCatalog(String catalogId, String token){
        ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
        if (adminCheckResult != null) {

            System.out.println("Unauthorized: " + adminCheckResult.getBody());
            return false;
        }
        ObjectId objectId = new ObjectId(catalogId);
        Optional<Catalog> optionalCatalog=catalogRepository.findById(objectId);
        if (optionalCatalog.isPresent()) {
            Catalog catalog = optionalCatalog.get();
            if (catalog.getDisabled()) {
                catalog.setDisabled(false);
                catalogRepository.save(catalog);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public ResponseEntity<Object> updateCatalog(String catalogId, CatalogDto updatedCatalogDto, String token) {
        try {
            String role = getRoleFromToken(token);
            if(!Objects.equals(role, "admin")){
                return new ResponseEntity<>("Only admin can edit catalog", HttpStatus.UNAUTHORIZED);
            }
            ObjectId objectId = new ObjectId(catalogId);
            Optional<Catalog> optionalCatalog = catalogRepository.findById(objectId);
            if (optionalCatalog.isPresent()) {
                Catalog existingCatalog = optionalCatalog.get();
                List<ObjectId> updatedUsers = updatedCatalogDto.getUsers();
                if (updatedUsers != null && !updatedUsers.isEmpty()) {
                    for (ObjectId userId : updatedUsers) {
                        if (!userExists(userId,token)) {
                            return new ResponseEntity<>("User with id: " + userId + " does not exist.", HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                List<ObjectId> updatedArticles = updatedCatalogDto.getArticles();
                if (updatedArticles != null && !updatedArticles.isEmpty()) {
                    for (ObjectId articleId : updatedArticles) {
                        if (!itemExists(articleId)) {
                            return new ResponseEntity<>("Item with id: " + articleId + " does not exist.", HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                List<ObjectId> updatedServices = updatedCatalogDto.getServices();
                if (updatedServices != null && !updatedServices.isEmpty()) {
                    for (ObjectId serviceId : updatedServices) {
                        if (!serviceExists(serviceId)) {
                            return new ResponseEntity<>("Service with id: " + serviceId + " does not exist.", HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                updateCatalogFields(existingCatalog, updatedCatalogDto);

                catalogRepository.save(existingCatalog);

                return new ResponseEntity<>("Catalog successfully updated.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Catalog with id: " + catalogId + " is not found.", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid catalog ID", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void updateCatalogFields(Catalog existingCatalog, CatalogDto updatedCatalogDto) {
        if (updatedCatalogDto.getName() != null) {
            existingCatalog.setName(updatedCatalogDto.getName());
        }

        if (updatedCatalogDto.getArticles() != null) {
            existingCatalog.setArticles(updatedCatalogDto.getArticles());
        }

        if (updatedCatalogDto.getServices() != null) {
            existingCatalog.setServices(updatedCatalogDto.getServices());
        }

        if (updatedCatalogDto.getUsers() != null) {
            existingCatalog.setUsers(updatedCatalogDto.getUsers());
        }

        /*if (updatedCatalogDto.getDisabled() != null) {
            existingCatalog.setDisabled(updatedCatalogDto.getDisabled());
        }*/

        existingCatalog.setDate_modified(LocalDateTime.now());

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

    private boolean userExists(ObjectId userId, String token) {
        try {
            String apiUrl = UriComponentsBuilder.fromUriString(userApiUrl)
                    .pathSegment(String.valueOf(userId))
                    .build()
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return true;
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            } else {
                System.out.println("Error while checking user existence. Status code: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while checking user existence: " + e.getMessage());
            return false;
        }
    }


    private boolean itemExists(ObjectId itemId) {
        try {
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            return optionalItem.isPresent();
        } catch (Exception e) {
            System.out.println("An error occurred while checking item existence: " + e.getMessage());
            return false;
        }
    }

    private boolean serviceExists(ObjectId serviceId) {
        try {
            Optional<hr.techtitans.items.models.Service> optionalService = serviceRepository.findById(serviceId);
            return optionalService.isPresent();
        } catch (Exception e) {
            System.out.println("An error occurred while checking service existence: " + e.getMessage());
            return false;
        }
    }
}
