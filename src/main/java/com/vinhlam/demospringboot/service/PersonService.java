package com.vinhlam.demospringboot.service;

import java.util.List;

import com.vinhlam.demospringboot.DTO.PersonDTO;
import com.vinhlam.demospringboot.entity.Person;
import com.vinhlam.demospringboot.entity.personObject.Language;

public interface PersonService {
	List<Person> getAll(int pageNo, int pageSize);
	PersonDTO getPersonById(String id);
	boolean deletePerson(String id);
	Person updatePerson(String id, PersonDTO personDTO);
	Person insertPerson(PersonDTO personDTO);
	
	boolean addNewLanguage(String id, Language language);
}
