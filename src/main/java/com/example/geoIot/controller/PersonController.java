package com.example.geoIot.controller;

import com.example.geoIot.entity.Person;
import com.example.geoIot.entity.dto.MsgDeleteDto;
import com.example.geoIot.entity.dto.RegisterPersonDto;
import com.example.geoIot.entity.dto.UpdatedPersonDto;
import com.example.geoIot.repository.PersonRepository;
import com.example.geoIot.service.device.DeviceTrackerRedisService;
import com.example.geoIot.service.person.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Pessoa - Controller", description = "Endpoints para gerenciamento de pessoas")
@RestController
@RequestMapping("/person")
@CrossOrigin
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private DeviceTrackerRedisService deviceTrackerRedisService;

    @PostMapping
    @Operation(summary = "Registrar uma nova pessoa", description = "Adiciona uma nova pessoa ao sistema com as informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pessoa registrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados fornecidos."),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar registrar a pessoa.")
    })
    public ResponseEntity<Person> addPerson(@RequestBody RegisterPersonDto pPerson){
         Person newPerson = new Person(pPerson);
         this.personService.savePerson(newPerson);
         this.deviceTrackerRedisService.onEvent();
         return ResponseEntity.status(201).body(newPerson);
    }

    @GetMapping
    @Operation(summary = "Listar todas as pessoas", description = "Retorna uma lista com todas as pessoas registradas no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a pessoa.")
    })
    public ResponseEntity<Set<Person>> getAllPersons(){
        Set<Person> persons = this.personService.getAllPersons();
        return ResponseEntity.status(200).body(persons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pessoa por ID", description = "Recupera os dados de uma pessoa específica usando o ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar buscar a pessoa.")
    })
    public ResponseEntity<Person> getPersonById(@PathVariable Long pId){
        Person person = this.personService.getPersonById(pId);
        return ResponseEntity.status(200).body(person);
    }

    @PutMapping
    @Operation(summary = "Atualizar pessoa", description = "Atualiza as informações de uma pessoa existente com base nos dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados fornecidos."),
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar atualizar a pessoa.")
    })
    public ResponseEntity<Person> updatePerson(@RequestBody UpdatedPersonDto pPerson){
        Person person = this.personService.updatePerson(pPerson);
        this.deviceTrackerRedisService.onEvent();
        return ResponseEntity.status(200).body(person);
    }


    @DeleteMapping
    @Operation(summary = "Deletar pessoa", description = "Remove uma pessoa do sistema com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pessoa deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Pessoa com o ID fornecido não foi encontrada."),
            @ApiResponse(responseCode = "408", description = "Tempo de resposta excedido."),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ao tentar deletar a pessoa.")
    })
    public ResponseEntity<MsgDeleteDto> deletePerson(@PathVariable Long pId){
        this.personService.deletePerson(pId);
        this.deviceTrackerRedisService.onEvent();
        MsgDeleteDto msg = new MsgDeleteDto("Person deleted successfully");
        return ResponseEntity.status(200).body(msg);
    }
}
