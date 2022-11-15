package com.vinhlam.demospringboot.controller;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinhlam.demospringboot.entity.Company;
import com.vinhlam.demospringboot.service.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {
	
	@Autowired
	private CompanyService companyService;
	
	@GetMapping("/getAllCompany")
	public ResponseEntity<?> countCompanyAndTotalEmployee(
			@RequestParam(value="pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value="pageSize", defaultValue = "5", required = false) int pageSize
		) {
		List<Company> companies = companyService.getAllCompany(pageNo, pageSize);
		if(companies == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Don't find company");
		} else {
			return ResponseEntity.ok(companies);
		}
	}
	
//  1. Thống kê có bao nhiêu công ty, số lượng nhân viên của công ty
	@GetMapping("/countCompanyAndTotalEmployee")
	public ResponseEntity<?> countCompanyAndTotalEmployee() {
		Document document = companyService.countCompanyAndTotalEmployee();
		if(document == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Don't find document");
		} else {
			return ResponseEntity.ok(document);
		}
	}

//  2. Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức lương phải trả cho những nhân viên đó là bao nhiêu
	@GetMapping("/statisticalCompany/{id}/{year}")
	public ResponseEntity<?> statisticalCompany(@PathVariable String id, @PathVariable int year) {
		Document document = companyService.statisticalCompany(id, year);
		if(document == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Don't find document");
		} else {
			return ResponseEntity.ok(document);
		}
	}
	
// 3. Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm trong năm 2022
	@GetMapping("/statisticalSalaryCompany/{year}")
	public ResponseEntity<?> statisticalSalaryCompany(@PathVariable int year) {
		List<Document> documents = companyService.statisticalSalaryCompany(year);
		if(documents == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Don't find document");
		} else {
			return ResponseEntity.ok(documents);
		}
	}
	
// 3. Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm trong năm 2022
	@GetMapping("/statisticalSalaryCompanyOrther/{purpose}")
	public ResponseEntity<?> statisticalSalaryCompanyOrther(@PathVariable String purpose, @RequestParam("startYear") int startYear, @RequestParam("endYear") int endYear) {
		List<Document> documents = companyService.statisticalSalaryCompanyOrther(purpose, startYear, endYear);
		if(documents == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Don't find document");
		} else {
			return ResponseEntity.ok(documents);
		}
	}
	
	
}
