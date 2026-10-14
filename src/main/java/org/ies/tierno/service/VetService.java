package org.ies.tierno.service;

import org.ies.tierno.controller.OwnerRequest;
import org.ies.tierno.controller.PetRequest;
import org.ies.tierno.model.Owner;
import org.ies.tierno.model.Pet;
import org.ies.tierno.model.Species;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VetService {

    private final Map<Long, Owner> owners = new ConcurrentHashMap<>();
    private final Map<Long, Pet> pets = new ConcurrentHashMap<>();
    private final AtomicLong nextOwnerId = new AtomicLong(1);
    private final AtomicLong nextPetId = new AtomicLong(1);

    public VetService() {
        Owner ana = createOwner(new OwnerRequest("Ana García", "ana@example.com", "612345678"));
        Owner luis = createOwner(new OwnerRequest("Luis Pérez", "luis@example.com", "698765432"));
        createOwner(new OwnerRequest("Marta Ruiz", "marta@example.com", "634567890"));
        createPet(ana.id(), new PetRequest("Toby", Species.DOG, LocalDate.of(2019, 4, 12)));
        createPet(ana.id(), new PetRequest("Misi", Species.CAT, LocalDate.of(2021, 9, 3)));
        createPet(luis.id(), new PetRequest("Kiwi", Species.BIRD, LocalDate.of(2023, 1, 20)));
    }

    /**
     * Devuelve los dueños cuyo nombre contiene el texto indicado,
     * sin distinguir mayúsculas. Si name es null, devuelve todos.
     */
    public List<Owner> findOwners(String name) {
        return owners.values().stream()
                .filter(owner -> name == null || owner.name().toLowerCase().contains(name.toLowerCase()))
                .sorted(Comparator.comparing(Owner::id))
                .toList();
    }

    /**
     * @throws OwnerNotFoundException
     */
    public Owner findOwner(long ownerId) {
        Owner owner = owners.get(ownerId);
        if (owner == null) {
            throw new OwnerNotFoundException(ownerId);
        }
        return owner;
    }

    /**
     * @throws DuplicateEmailException
     */
    public synchronized Owner createOwner(OwnerRequest request) {
        checkEmailAvailable(request.email(), null);
        long id = nextOwnerId.getAndIncrement();
        Owner owner = new Owner(id, request.name(), request.email(), request.phone());
        owners.put(id, owner);
        return owner;
    }

    /**
     * @throws OwnerNotFoundException
     * @throws DuplicateEmailException si el email pertenece a otro dueño
     */
    public synchronized Owner updateOwner(long ownerId, OwnerRequest request) {
        findOwner(ownerId);
        checkEmailAvailable(request.email(), ownerId);
        Owner owner = new Owner(ownerId, request.name(), request.email(), request.phone());
        owners.put(ownerId, owner);
        return owner;
    }

    /**
     * Solo se puede eliminar un dueño que no tenga mascotas.
     *
     * @throws OwnerNotFoundException
     * @throws OwnerHasPetsException
     */
    public synchronized void deleteOwner(long ownerId) {
        findOwner(ownerId);
        boolean hasPets = pets.values().stream().anyMatch(pet -> pet.ownerId() == ownerId);
        if (hasPets) {
            throw new OwnerHasPetsException(ownerId);
        }
        owners.remove(ownerId);
    }

    /**
     * Devuelve las mascotas del dueño, filtradas por especie si species no es null.
     *
     * @throws OwnerNotFoundException
     */
    public List<Pet> findPets(long ownerId, Species species) {
        findOwner(ownerId);
        return pets.values().stream()
                .filter(pet -> pet.ownerId() == ownerId)
                .filter(pet -> species == null || pet.species() == species)
                .sorted(Comparator.comparing(Pet::id))
                .toList();
    }

    /**
     * @throws OwnerNotFoundException
     * @throws PetNotFoundException si la mascota no existe o pertenece a otro dueño
     */
    public Pet findPet(long ownerId, long petId) {
        findOwner(ownerId);
        Pet pet = pets.get(petId);
        if (pet == null || pet.ownerId() != ownerId) {
            throw new PetNotFoundException(ownerId, petId);
        }
        return pet;
    }

    /**
     * @throws OwnerNotFoundException
     */
    public synchronized Pet createPet(long ownerId, PetRequest request) {
        findOwner(ownerId);
        long id = nextPetId.getAndIncrement();
        Pet pet = new Pet(id, ownerId, request.name(), request.species(), request.birthDate());
        pets.put(id, pet);
        return pet;
    }

    /**
     * @throws OwnerNotFoundException
     * @throws PetNotFoundException
     */
    public synchronized Pet updatePet(long ownerId, long petId, PetRequest request) {
        findPet(ownerId, petId);
        Pet pet = new Pet(petId, ownerId, request.name(), request.species(), request.birthDate());
        pets.put(petId, pet);
        return pet;
    }

    /**
     * @throws OwnerNotFoundException
     * @throws PetNotFoundException
     */
    public synchronized void deletePet(long ownerId, long petId) {
        findPet(ownerId, petId);
        pets.remove(petId);
    }

    private void checkEmailAvailable(String email, Long excludedOwnerId) {
        boolean inUse = owners.values().stream()
                .anyMatch(owner -> owner.email().equalsIgnoreCase(email) && !owner.id().equals(excludedOwnerId));
        if (inUse) {
            throw new DuplicateEmailException(email);
        }
    }
}
