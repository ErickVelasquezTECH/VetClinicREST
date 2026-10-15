package org.ies.tierno.controller;

import jakarta.validation.Valid;
import org.ies.tierno.controller.dto.OwnerRequest;
import org.ies.tierno.controller.dto.OwnerResponse;
import org.ies.tierno.service.DuplicateEmailException;
import org.ies.tierno.service.OwnerHasPetsException;
import org.ies.tierno.service.OwnerNotFoundException;
import org.ies.tierno.service.VetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final VetService service;


    public OwnerController(VetService service) {
        this.service = service;
    }

    @GetMapping
    public List<OwnerResponse> getOwner(
            @RequestParam(required = false) String name) {
        return service.findOwners(name)
                .stream()
                .map(OwnerResponse::from)
                .toList();
    }

    @GetMapping("/{ownerId}")
    public OwnerResponse getOwnerId(@PathVariable long ownerId) {
        return OwnerResponse.from(service.findOwner(ownerId));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerResponse postOwner(
            @Valid @RequestBody OwnerRequest request
    ) {
        return OwnerResponse.from(service.createOwner(request));
    }


    @PutMapping("/{ownerId}")
    public OwnerResponse updateOwner(
            @PathVariable long ownerId,
            @Valid @RequestBody OwnerRequest request) {
        return OwnerResponse.from(service.updateOwner(ownerId, request));
    }

    @DeleteMapping("/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnerId(
            @PathVariable long ownerId
    ){
        service.deleteOwner(ownerId);
    }

    @ExceptionHandler(OwnerHasPetsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleOwnerNotFound(OwnerHasPetsException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleOwnerNotFound(OwnerNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateEmail(DuplicateEmailException e) {
        return Map.of("error", e.getMessage());
    }


}
