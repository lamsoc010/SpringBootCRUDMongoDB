package com.vinhlam.demospringboot.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vinhlam.demospringboot.entity.EmployeeObject.Salary;

import lombok.Data;

@Data
@Document(collection = "employee")
public class Employee {
	
	@Id
	private String _id;
	private String idPerson;
	private String idCompany;
	private Date timeJoin;
	private List<Salary> salary;
	private int status;
}
