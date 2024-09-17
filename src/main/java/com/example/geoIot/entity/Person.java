package com.example.geoIot.entity;

import com.example.geoIot.entity.dto.RegisterPersonDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_person")
    private Long idPerson;
    @Column(name ="ito_person_code")
    private String idText;
    @Column(name ="name")
    private String fullName;
    @Column(name ="device_code")
    private String codeDevice;

    public Person(RegisterPersonDto pPerson) {
        this.idText = pPerson.idPersonText();
        this.fullName = pPerson.fullName();
        this.codeDevice = pPerson.codeDevice();
    }

}
