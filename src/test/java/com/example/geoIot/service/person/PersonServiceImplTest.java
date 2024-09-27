package com.example.geoIot.service.person;

import com.example.geoIot.exception.PersonAlreadyExistsException;
import com.example.geoIot.exception.PersonNotFoundException;
import com.example.geoIot.entity.Person;
import com.example.geoIot.entity.dto.UpdatedPersonDto;
import com.example.geoIot.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    @InjectMocks
    private PersonServiceImpl personService;
    @Mock
    private PersonRepository repository;

    private Person person;

    @Test
    @DisplayName("should register a person with all the correct values")
    void shouldRegisterPerson(){
        this.person = new Person(
                null,
                "1640966C-BBAC-4A26-8A06-0670296D361C",
                "Darwin Yoel Franco Vasquez",
                "1435"
        );
        BDDMockito.given(repository.findByIdText(person.getIdText())).willReturn(Optional.empty());
        assertDoesNotThrow(() ->personService.savePerson(person));
    }

    @Test
    @DisplayName("Should throw an exception when the person already exists in the system.")
    void exceptionShouldThrowAnExceptionWhenThePersonAlreadyExists(){
         this.person = new Person(
                 null,
                "1640966C-BBAC-4A26-8A06-0670296D361C",
                "Darwin Yoel Franco Vasquez",
                "1435"
        );
        BDDMockito.given(repository.findByIdText(person.getIdText())).willReturn(Optional.of(person));
        assertThrowsExactly(PersonAlreadyExistsException.class, () ->personService.savePerson(person));
    }

    @Test
    @DisplayName("Should throw an exception when any data of the person is invalid.")
    void exceptionShouldThrowAnExceptionWhenAnyDataOfThePersonIsInvalid(){
         this.person = new Person(
                 null,
                "",
                "Darwin Yoel Franco Vasquez",
                "1435"
        );
        assertThrowsExactly(IllegalArgumentException.class, () ->personService.savePerson(person));
    }

    @Test
    @DisplayName("Should throw an exception if the person ID is invalid.")
    void getPersonByIdWithInvalidPersonId(){
        BDDMockito.given(repository.findById(1l)).willReturn(Optional.empty());
        assertThrowsExactly(PersonNotFoundException.class, ()-> personService.getPersonById(1l));
    }

    @Test
    @DisplayName("Should ensure that the user update is performed correctly.")
    void updateValid(){
        this.person = new Person(
                1l,
                "",
                "Darwin Yoel Franco Vasquez",
                "1435"
        );
        UpdatedPersonDto personUpdatedDto = new UpdatedPersonDto(1l,
                null,
                "1439");
        BDDMockito.given(repository.findById(1l)).willReturn(Optional.of(person));
        assertEquals(personUpdatedDto.codeDevice(), personService.updatePerson(personUpdatedDto).getCodeDevice());
    }

}