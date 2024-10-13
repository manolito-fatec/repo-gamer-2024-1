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
public class AddressDto {

    @JsonProperty("town")
    private String town;
    @JsonProperty("road")
    private String road;
    @JsonProperty("state")
    private String state;
    @JsonProperty("country")
    private String country;
}
