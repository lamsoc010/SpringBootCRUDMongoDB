package com.vinhlam.demospringboot.entity.CompanyObject;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data

public class UnitMoney {
	private int type;
	private String unit;
}
