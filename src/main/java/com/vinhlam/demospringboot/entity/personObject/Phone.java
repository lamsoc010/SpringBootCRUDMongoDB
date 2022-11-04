package com.vinhlam.demospringboot.entity.personObject;

import lombok.Data;

@Data
public class Phone {
	private String areaCode;
	private String network;
	private String phone;
	private int status;
}
