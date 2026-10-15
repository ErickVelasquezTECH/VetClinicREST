package org.ies.tierno.controller.dto;


import org.ies.tierno.model.Owner;

public record OwnerResponse(
        String name,
        String email,
        String phone
) {
    public static OwnerResponse from (Owner owner){
        return new OwnerResponse(
                owner.name(), owner.email(),
                owner.phone()
        );
    }

}
