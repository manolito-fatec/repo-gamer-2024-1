package com.example.geoIot.entity.dto;

public record UpdatedPersonDto(
        Long idPerson,
        String fullName,
        String codeDevice
) {
}
