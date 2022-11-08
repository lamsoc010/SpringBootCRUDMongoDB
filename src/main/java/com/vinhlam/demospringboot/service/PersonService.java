package com.vinhlam.demospringboot.service;

import java.util.List;

import org.bson.Document;
import org.springframework.http.ResponseEntity;

import com.vinhlam.demospringboot.DTO.PersonDTO;
import com.vinhlam.demospringboot.entity.Person;
import com.vinhlam.demospringboot.entity.Result;
import com.vinhlam.demospringboot.entity.personObject.Info;
import com.vinhlam.demospringboot.entity.personObject.Language;

public interface PersonService {
	ResponseEntity<?> getAll(int pageNo, int pageSize);
	ResponseEntity<?> getPersonById(String id);
	ResponseEntity<?> deletePerson(String id);
	ResponseEntity<?> updatePerson(String id, PersonDTO personDTO);
	Person insertPerson(PersonDTO personDTO);
	
	ResponseEntity<?> addNewLanguage(String id, Language language);
	ResponseEntity<?> deleteLanguage(String id, Language language);
	ResponseEntity<?> addNewInfo(String id, Info info);
	ResponseEntity<?> deacticeCMNDUser(String id, Info info);
	ResponseEntity<?> deleteInfo(String id, Info info);
	ResponseEntity<?> updateAllSexToNA();
	ResponseEntity<?> countTotalPhones();
	ResponseEntity<?> getAllLanguageDistinct();
	ResponseEntity<?> getPersonByName(String firstName);
	List<Person> searchPersonByName(String name);
}
