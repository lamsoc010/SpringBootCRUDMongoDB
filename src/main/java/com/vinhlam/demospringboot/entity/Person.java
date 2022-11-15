package com.vinhlam.demospringboot.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vinhlam.demospringboot.entity.personObject.Email;
import com.vinhlam.demospringboot.entity.personObject.Info;
import com.vinhlam.demospringboot.entity.personObject.Language;
import com.vinhlam.demospringboot.entity.personObject.Phone;

import lombok.Data;

@Data
@Document(collection = "person")
public class Person {
	
	@Id
	private String _id;
	private String firstName;
	private String lastName;
	private Date birthday;
	private int age;
	private Integer sex;
	private List<Info> infos;
	private List<Email> emails;
	private List<Phone> phones;
	private List<Language> languages;
	
	
}
