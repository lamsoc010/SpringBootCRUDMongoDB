package com.vinhlam.demospringboot.service;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.http.ResponseEntity;

import com.vinhlam.demospringboot.DTO.PersonDTO;
import com.vinhlam.demospringboot.entity.Person;
import com.vinhlam.demospringboot.entity.personObject.Info;
import com.vinhlam.demospringboot.entity.personObject.Language;

public interface PersonService {
	List<PersonDTO> getAll(int pageNo, int pageSize);
	PersonDTO getPersonById(String id);
	int deletePersonById(String id);
	int updatePersonById(String id, PersonDTO personDTO);
	boolean insertPerson(PersonDTO personDTO);
	
	int addNewLanguage(String id, Language language);
	int deleteLanguage(String id, Language language);
	int addNewInfo(String id, Info info);
	int deacticeCMNDUser(String id, Info info);
	int deleteInfo(String id, Info info);
	boolean updateAllSexToNA();
	Document countTotalPhones();
	List<String> getAllLanguageDistinct();
	List<PersonDTO> getPersonByName(String firstName, int startMonth, int endMonth);
	
	List<Person> searchPersonByName(String name);
//	Làm thêm
	ResponseEntity<?> addNewLanguageAndDeleteInfo(String id, Language language, Info info);
	
//	Câu 14 
	int updateMutilFieldPerson(String id, Person person);
	
}
