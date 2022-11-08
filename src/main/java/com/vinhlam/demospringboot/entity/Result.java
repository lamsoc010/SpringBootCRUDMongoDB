package com.vinhlam.demospringboot.entity;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class Result {
	private String body;
	private HttpStatus httpStatus;
	public Result(String body, HttpStatus httpStatus) {
		super();
		this.body = body;
		this.httpStatus = httpStatus;
	}
	
	
}
