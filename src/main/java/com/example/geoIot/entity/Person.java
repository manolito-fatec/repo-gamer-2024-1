package com.example.geoIot.entity;

import com.example.geoIot.entity.dto.RegisterPersonDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(idPerson, person.idPerson) && Objects.equals(idText, person.idText) && Objects.equals(fullName, person.fullName) && Objects.equals(codeDevice, person.codeDevice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPerson, idText, fullName, codeDevice);
    }
}
