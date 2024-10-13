package com.example.geoIot.entity.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class LocationDto {

    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private AddressDto address;
}
