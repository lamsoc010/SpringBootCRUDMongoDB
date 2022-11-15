package com.vinhlam.demospringboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/persons")
public class PersonController {

	@Autowired
	private PersonService personService;

	@GetMapping("/getAll")
	public ResponseEntity<?> getAll(@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {
		List<PersonDTO> personDTOs = new ArrayList<>();
		personDTOs = personService.getAll(pageNo, pageSize);

		if (personDTOs == null || personDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List Person is null");
		}

		return ResponseEntity.ok(personDTOs);
	}

	@GetMapping("/getPersonById/{id}")
	public ResponseEntity<?> getPersonById(@PathVariable String id) {
//		Check input Id
		if (!ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id is not ObjectId");
		}

		PersonDTO personDTO = personService.getPersonById(id);
		if (personDTO == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person is null");
		}
		return ResponseEntity.ok(personDTO);
	}

	@PostMapping("/createNewPerson")
	public ResponseEntity<?> createNewPerson(@RequestBody PersonDTO personDTO) {
		if (personDTO == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input Error");
		}

		boolean resultCheck = personService.insertPerson(personDTO);

		if (resultCheck) {
			return ResponseEntity.ok("Create new Person success");
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Insert Person is failed");
		}
	}

	@DeleteMapping("/deletePersonById/{id}")
	public ResponseEntity<?> deletePersonById(@PathVariable String id) {
//		Check input Id
		if (!ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id is not ObjectId");
		}

		int resultCode = personService.deletePersonById(id);

		if (resultCode == 0) { 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		} else if (resultCode == 1) {
			return ResponseEntity.ok("Delete Person is Id: " + id + "success");
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete Person is failed");
	}

	@PostMapping("/updatePerson/{id}")
	public ResponseEntity<?> updatePersonById(@PathVariable String id, @RequestBody PersonDTO personDTO) {
//		Check input Id
		if (!ObjectId.isValid(id) || personDTO == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id is not ObjectId");
		}

		int resultCode = personService.updatePersonById(id, personDTO);
		if (resultCode == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		} else if (resultCode == 1) {
			return ResponseEntity.ok("Update Person is Id: " + id + "success");
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update Person is failed");

	}

//	Tìm kiếm person theo tên
	@GetMapping("/searchPersonByName/{name}")
	public ResponseEntity<?> searchPersonByName(@PathVariable String name) {
		List<Person> persons = personService.searchPersonByName(name);
		System.out.println(name);
		if (persons.size() > 0) {
			return ResponseEntity.ok(persons);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person is null");
		}
	}

//2. Viết query update thêm 1 language của 1 person
	@PostMapping("/language/update/{id}")
	public ResponseEntity<?> addNewLanguage(@PathVariable String id, @RequestBody Language language) {
		if (!ObjectId.isValid(id) || language == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input Error");
		}

		int resultCode = personService.addNewLanguage(id, language);
		if (resultCode == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		} else if (resultCode == 1) {
			return ResponseEntity.ok("Update language in Person is Id: " + id + " success");
		} else if (resultCode == 3) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Language in Person don't exist");
		}

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update language in Person is failed");

	}

//3. Viết query xoá 1 language của 1 person
	@GetMapping("/language/delete/{id}")
	public ResponseEntity<?> deleteLanguage(@PathVariable String id, @RequestBody Language language) {
//		Check input Id
		if (!ObjectId.isValid(id) || language == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input Error");
		}

		int resultCode = personService.addNewLanguage(id, language);

		if (resultCode == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		} else if (resultCode == 1) {
			return ResponseEntity.ok("Delete language in Person is Id: " + id + " success");
		} else if (resultCode == 3) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Language in Person don't exist");
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete language in Person is failed");
	}

//4. Viết query update thêm 1 info của 1 person
	@PostMapping("/info/createNew/{id}")
	public ResponseEntity<?> addNewInfo(@PathVariable String id, @RequestBody Info info) {
//		Check input 
		if (!ObjectId.isValid(id) || info == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input Error");
		}

		int resultCode = personService.addNewInfo(id, info);

		if (resultCode == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		} else if (resultCode == 1) {
			return ResponseEntity.ok("Create new info in Person is Id: " + id + " success");
		} else if (resultCode == 3) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Info in Person don't exist");
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Create new info in Person is failed");

	}

//5. Viết query update CMND của 1 user thành deactice (ko còn sử dụng nữa)
	@PostMapping("/info/updateDeactice/{id}")
	public ResponseEntity<?> deacticeCMNDUser(@PathVariable String id, @RequestBody Info info) {
//		Check input 
		if (!ObjectId.isValid(id) || info == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input Error");
		}

		int resultCode = personService.deacticeCMNDUser(id, info);

		if (resultCode == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		} else if (resultCode == 1) {
			return ResponseEntity.ok("Update status info in Person is Id: " + id + " success");
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update status info in Person is failed");
		}
	}

//6. Viết query xoá 1 info của 1 person	
	@DeleteMapping("/info/deleteById/{id}")
	public ResponseEntity<?> deleteInfo(@PathVariable String id, @RequestBody Info info) {
//		Check input Id
		if (!ObjectId.isValid(id) || info == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input Error");
		}

		int resultCode = personService.deleteInfo(id, info);

		if (resultCode == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		} else if (resultCode == 1) {
			return ResponseEntity.ok("Delete info in Person is Id: " + id + " success");
		} else if (resultCode == 3) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Info in Person don't exist");
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete Info in Person is failed");
	}

//8. Viết query cập nhật giới tính của toàn bộ document trong collection person sang NA (Chưa xác định)
	@GetMapping("/update/updateSexToNA")
	public ResponseEntity<?> updateAllSexToNA() {
		boolean resultCheck =  personService.updateAllSexToNA();
		if(resultCheck) {
			return ResponseEntity.ok("Update sex all document to NA success");
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Update sex is failed");
		}
	}

//9. Viết query đếm trong collection person có bao  nhiêu sdt
	@GetMapping("/getTotalAllPhones")
	public ResponseEntity<?> countTotalPhones() {
		Document document = personService.countTotalPhones();
		if(document == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Don't count total phones");
		} else {
			return ResponseEntity.ok(document);
		}
	}

//10. Viết query get toàn bộ language hiện có trong collection person (kết quả ko được trùng nhau)
	@GetMapping("/getAllLanguagesDistinct")
	public ResponseEntity<?> getAllLanguageDistinct() {
		List<String> languages = personService.getAllLanguageDistinct();
		if(languages == null || languages.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Info in Person don't exist");
		} else {
			return ResponseEntity.ok(languages);
		}
	}

// 11. Viết query get những person có tên chứa "Nguyễn" và ngày sinh trong khoảng tháng 2~ tháng 10
	@GetMapping("/getPersonByFirstName/{firstName}")
	public ResponseEntity<?> getPersonByName(@PathVariable String firstName, @RequestParam("startMonth") Integer startMonth, @RequestParam("endMonth") Integer endMonth) {
		if(firstName == null || startMonth == null || endMonth == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input Error");
		}
		List<PersonDTO> personDTOs = personService.getPersonByName(firstName, startMonth, endMonth);
		if(personDTOs == null || personDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Don't find Person by name");
		} else {
			return ResponseEntity.ok(personDTOs);
		}
	}


//	14.
	@PostMapping("/updateMutilFieldPerson/{id}")
	public ResponseEntity<?> updateMutilFieldPerson(@PathVariable String id, @RequestBody Person person) {
//		Check input data
		if (!ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input data is incorrect");
		} else if (person == null || person.getSex() == null || person.getLanguages() == null
				|| person.getInfos() == null || person.getPhones() == null || person.getLanguages().size() == 0
				|| person.getInfos().size() == 0 || person.getPhones().size() == 0
				|| person.getLanguages().get(0).getLanguage() == null || person.getPhones().get(0).getPhone() == null
				|| person.getInfos().get(0).getIdNo() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input data is incorrect");
		}

		int resultCode = personService.updateMutilFieldPerson(id, person);
//		Return respon to client
		if (resultCode == 1) {
			return ResponseEntity.status(HttpStatus.OK).body("Update person success");
		} else if (resultCode == 0) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Person don't exist");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Update person failed");
		}
	}

}
