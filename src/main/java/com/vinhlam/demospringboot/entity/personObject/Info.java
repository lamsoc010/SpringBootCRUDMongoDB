package com.vinhlam.demospringboot.entity.personObject;

import java.util.Date;

import lombok.Data;

@Data
public class Info {
	private int type;
	private String idNo;
	private Date issuedOn;
	private int status;
	
}
