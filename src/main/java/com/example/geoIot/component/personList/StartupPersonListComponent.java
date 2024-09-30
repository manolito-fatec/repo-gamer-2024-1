package com.example.geoIot.component.personList;

import com.example.geoIot.entity.Person;
import com.example.geoIot.repository.PersonRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class StartupPersonListComponent implements ApplicationRunner {

    @Autowired
    private PersonRepository personRepository;

    @Getter
    private static Set<Person> persons;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Person> allPersons = personRepository.findAll();
        persons = new HashSet<>(allPersons);
    }

}
