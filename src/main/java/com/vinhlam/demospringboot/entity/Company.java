package com.vinhlam.demospringboot.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vinhlam.demospringboot.entity.CompanyObject.UnitMoney;

import lombok.Data;

@Data
@Document(collection = "company")
public class Company {
	@Id
	private String _id;
	private String name;
	private String address;
	private int maxEmployee;
	private int currentEmployee;
	private List<String> purposes;
	private List<UnitMoney> unitMoney;
}
