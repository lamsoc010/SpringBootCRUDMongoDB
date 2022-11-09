package com.vinhlam.demospringboot.controller;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
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
import com.vinhlam.demospringboot.entity.personObject.Info;
import com.vinhlam.demospringboot.entity.personObject.Language;
import com.vinhlam.demospringboot.service.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	private PersonService personService;
	
	@GetMapping("/getAll")
	public ResponseEntity<?> getAll(
			@RequestParam(value="pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value="pageSize", defaultValue = "5", required = false) int pageSize
	) {
		return personService.getAll(pageNo, pageSize);
	}
	
	
	@GetMapping("/getPersonById/{id}")
	public ResponseEntity<?> getPersonById(@PathVariable String id) {
		return personService.getPersonById(id);
	}
	
	@PostMapping("/createNewPerson") 
	public ResponseEntity<?> createNewPerson(@RequestBody PersonDTO personDTO) {
		Person person = personService.insertPerson(personDTO);
		if(person != null) {
			return ResponseEntity.ok("insert success");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insert not success");
		}
	}
	
	@DeleteMapping("/deletePersonById/{id}")
	public ResponseEntity<?> delete(@PathVariable String id) {
		return personService.deletePerson(id);
	}
	

	@PostMapping("/updatePerson/{id}")
	public ResponseEntity<?> update(@PathVariable String id, @RequestBody PersonDTO personDTO) {
		return personService.updatePerson(id, personDTO);
	}
	
//	Tìm kiếm person theo tên
	@GetMapping("/searchPersonByName/{name}")
	public ResponseEntity<?> searchPersonByName(@PathVariable String name) {
		List<Person> persons = personService.searchPersonByName(name);
		System.out.println(name);
		if(persons.size() > 0) {
			return ResponseEntity.ok(persons);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person is null");
		}
	}
	
//2. Viết query update thêm 1 language của 1 person
	@PostMapping("/language/update/{id}")
	public ResponseEntity<?> updateLanguage(@PathVariable String id, @RequestBody Language language) {
		if(ObjectId.isValid(id)) {
			return personService.addNewLanguage(id, language);			
		} else {
			return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Id is not Object Id");
		}
	}
	
//3. Viết query xoá 1 language của 1 person
	@GetMapping("/language/delete/{id}")
	public ResponseEntity<?> deleteLanguage(@PathVariable String id, @RequestBody Language language) {
		return personService.deleteLanguage(id, language);
	}
	
//4. Viết query update thêm 1 info của 1 person
	@PostMapping("/info/createNew/{id}")
	public ResponseEntity<?> addNewInfo(@PathVariable String id, @RequestBody Info info) {
		return personService.addNewInfo(id, info);
	}
	
//5. Viết query update CMND của 1 user thành deactice (ko còn sử dụng nữa)
	@PostMapping("/info/updateDeactice/{id}")
	public ResponseEntity<?> deacticeCMNDUser(@PathVariable String id, @RequestBody Info info) {
		return personService.deacticeCMNDUser(id, info);
	}
	
//6. Viết query xoá 1 info của 1 person	
	@DeleteMapping("/info/deleteById/{id}")
	public ResponseEntity<?> deleteInfo(@PathVariable String id, @RequestBody Info info) {
		return personService.deleteInfo(id, info);
	}
	
//8. Viết query cập nhật giới tính của toàn bộ document trong collection person sang NA (Chưa xác định)
	@GetMapping("/update/updateSexToNA")
	public ResponseEntity<?> updateAllSexToNA() {
		return personService.updateAllSexToNA();
	}
	
//9. Viết query đếm trong collection person có bao  nhiêu sdt
	@GetMapping("/getTotalAllPhones")
	public ResponseEntity<?> countTotalPhones() {
		return personService.countTotalPhones();
	}
//10. Viết query get toàn bộ language hiện có trong collection person (kết quả ko được trùng nhau)
	@GetMapping("/getAllLanguagesDistinct")
	public ResponseEntity<?> getAllLanguageDistinct() {
		return personService.getAllLanguageDistinct();
	}
	
// 11. Viết query get những person có tên chứa "Nguyễn" và ngày sinh trong khoảng tháng 2~ tháng 10
	@GetMapping("/getPersonByFirstName/{firstName}")
	public ResponseEntity<?> getPersonByName(@PathVariable String firstName) {
		return personService.getPersonByName(firstName);
	}

//** Làm thêm
	@PostMapping("/languageInfo/{id}")
	public ResponseEntity<?> addNewLanguageAndDeleteInfo(@PathVariable String id, @RequestBody Language language, @RequestBody Info info) {
		if(ObjectId.isValid(id)) {
			return personService.addNewLanguageAndDeleteInfo(id, language, info);			
		} else {
			return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Id is not Object Id");
		}
	}
	
}
