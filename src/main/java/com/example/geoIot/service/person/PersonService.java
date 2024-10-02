package com.example.geoIot.service.person;

import com.example.geoIot.entity.Person;
import com.example.geoIot.entity.dto.UpdatedPersonDto;

import java.util.Set;

public interface PersonService {

    public Person getPersonById(Long pId);

    public void savePerson(Person pPerson);

    public Person updatePerson(UpdatedPersonDto pPerson);

    public void deletePerson(Long pId);

    public Set<Person> getAllPersons();

    public void validatePerson(Person pPerson);

    public Person findByFullName(String fullName);
}
