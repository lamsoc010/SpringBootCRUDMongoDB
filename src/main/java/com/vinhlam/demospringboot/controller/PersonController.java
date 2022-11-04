package com.vinhlam.demospringboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinhlam.demospringboot.DTO.PersonDTO;
import com.vinhlam.demospringboot.entity.Person;
import com.vinhlam.demospringboot.entity.personObject.Language;
import com.vinhlam.demospringboot.service.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	private PersonService personService;
	
	@GetMapping("")
	public ResponseEntity<?> getAll(
			@RequestParam(value="pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value="pageSize", defaultValue = "5", required = false) int pageSize
	) {
		List<Person> listPerson = personService.getAll(pageNo, pageSize);
		System.out.println(listPerson.size());
		if(listPerson.size() > 0) {
			return ResponseEntity.ok(listPerson);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List person is null");
		}
	}
	
	
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getAll(@PathVariable String id) {
		PersonDTO personDTO = personService.getPersonById(id);
		if(personDTO != null) {
			return ResponseEntity.ok(personDTO);
		} else {
			System.out.println("Lỗi");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person is null");
		}
	}
	
	@PostMapping("/insert") 
	public ResponseEntity<?> insert(@RequestBody PersonDTO personDTO) {
		Person person = personService.insertPerson(personDTO);
		if(person != null) {
			return ResponseEntity.ok("insert success");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insert not success");
		}
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable String id) {
		boolean result = personService.deletePerson(id);
		if(result) {
			return ResponseEntity.ok("delete success");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete not success");
		}
	}
	

	@PostMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable String id, @RequestBody PersonDTO personDTO) {
		Person person = personService.updatePerson(id, personDTO);
		if(person != null) {
			return ResponseEntity.ok(person);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update not success");
		}
	}
	
//	Thêm mới 1 language
	@PostMapping("/update/language/{id}")
	public ResponseEntity<?> updateLanguage(@PathVariable String id, @RequestBody Language language) {
		boolean result = personService.addNewLanguage(id, language);
		if(result) {
			return ResponseEntity.ok("Add new language succees");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Add new language not success");
		}
	}
}
