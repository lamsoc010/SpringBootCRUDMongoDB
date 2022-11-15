package com.vinhlam.demospringboot.service;

import java.util.List;

import org.bson.Document;

import com.vinhlam.demospringboot.entity.Company;

public interface CompanyService {
	List<Company> getAllCompany(int pageNo, int pageSize);
	Company getCompanyById(String id);
	boolean updateCompany(Company company);
	boolean deleteCompany(Company company);
	
// 1. Thống kê có bao nhiêu công ty, số lượng nhân viên của công ty
	Document countCompanyAndTotalEmployee();
	
// 2. Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức lương phải trả cho những nhân viên đó là bao nhiêu
	Document statisticalCompany(String id, int year);
	
// 3. Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm trong năm 2022
	List<Document> statisticalSalaryCompany(int year);

// 4. Thống kê tổng số tiền các công ty IT phải trả cho những người đăng ký vào làm trong các năm từ 2020 ~ 2022
	List<Document> statisticalSalaryCompanyOrther(String purpose, int startYear, int endYear);
}
