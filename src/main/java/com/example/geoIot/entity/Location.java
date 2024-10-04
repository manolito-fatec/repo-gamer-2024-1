package com.example.geoIot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oracle.spatial.geometry.JGeometry;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_location")
    private Long idLocation;
    @Column(name = "name")
    private String name;
    @Column(name = "poly")
    private JGeometry poly;
}
