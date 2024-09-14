package com.example.geoIot.controller;

import com.example.geoIot.entity.Person;
import com.example.geoIot.entity.dto.MsgDeleteDto;
import com.example.geoIot.entity.dto.RegisterPersonDto;
import com.example.geoIot.entity.dto.UpdatedPersonDto;
import com.example.geoIot.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/person")
@CrossOrigin
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    public ResponseEntity<Person> addPerson(@RequestBody RegisterPersonDto pPerson){
         Person newPerson = new Person(pPerson);
         this.personService.savePerson(newPerson);
         return ResponseEntity.status(201).body(newPerson);
    }

    @GetMapping
    public ResponseEntity<Set<Person>> getAllPersons(){
        Set<Person> persons = this.personService.getAllPersons();
        return ResponseEntity.status(200).body(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long pId){
        Person person = this.personService.getPersonById(pId);
        return ResponseEntity.status(200).body(person);
    }

    @PutMapping
    public ResponseEntity<Person> updatePerson(@RequestBody UpdatedPersonDto pPerson){
        Person person = this.personService.updatePerson(pPerson);
        return ResponseEntity.status(200).body(person);
    }


    @DeleteMapping
    public ResponseEntity<MsgDeleteDto> deletePerson(@PathVariable Long pId){
        this.personService.deletePerson(pId);
        MsgDeleteDto msg = new MsgDeleteDto("Person deleted successfully");
        return ResponseEntity.status(200).body(msg);
    }
}
