package org.ies.tierno.controller;

import jakarta.validation.Valid;
import org.ies.tierno.controller.dto.PetRequest;
import org.ies.tierno.controller.dto.PetResponse;
import org.ies.tierno.model.Species;
import org.ies.tierno.service.OwnerNotFoundException;
import org.ies.tierno.service.PetNotFoundException;
import org.ies.tierno.service.VetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owners/{ownerId}/pets")
public class PetController {
    private final VetService service;


    public PetController(VetService service) {
        this.service = service;
    }

    @GetMapping
    public List<PetResponse> getPets(
            @PathVariable long ownerId,
            @RequestParam(required = false) Species species) {
        return service.findPets(ownerId, species)
                .stream()
                .map(PetResponse::from)
                .toList();
    }

    @GetMapping("/{petId}")
    public PetResponse getPetId(
            @PathVariable long ownerId,
            @PathVariable long petId
    ) {
        return PetResponse.from(service.findPet(ownerId, petId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse postPet(
            @PathVariable long ownerId,
            @Valid @RequestBody PetRequest request
    ) {
        return PetResponse.from(service.createPet(ownerId, request));
    }


    @PutMapping("/{petId}")
    public PetResponse putPetId(
            @PathVariable long ownerId,
            @PathVariable long petId,
            @Valid @RequestBody PetRequest request
    ) {
        return PetResponse.from(service.updatePet(ownerId, petId, request));
    }


    @DeleteMapping("/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(
            @PathVariable long ownerId,
            @PathVariable long petId
    ) {
        service.deletePet(ownerId, petId);
    }

    @ExceptionHandler(PetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleOwnerNotFound(PetNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleOwnerNotFound(OwnerNotFoundException e) {
        return Map.of("error", e.getMessage());
    }
}
