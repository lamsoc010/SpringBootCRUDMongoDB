package com.vinhlam.demospringboot.DTO;

import java.util.Date;
import java.util.List;

import com.vinhlam.demospringboot.entity.personObject.Email;
import com.vinhlam.demospringboot.entity.personObject.Info;
import com.vinhlam.demospringboot.entity.personObject.Language;
import com.vinhlam.demospringboot.entity.personObject.Phone;

import lombok.Data;

@Data
public class PersonDTO {

	private String id;
	private String firstName;
	private String lastName;
	private Date birthday;
	private int age;
	private int sex;
	
	private List<Email> emails;
	private List<Phone> phones;
	private List<Language> languages;
}
