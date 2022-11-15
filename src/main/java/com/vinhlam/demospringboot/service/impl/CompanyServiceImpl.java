package com.vinhlam.demospringboot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.vinhlam.demospringboot.entity.Company;
import com.vinhlam.demospringboot.repository.CompanyRepository;
import com.vinhlam.demospringboot.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService{

	@Autowired
	private MongoDatabase mongoDatabase;
	@Autowired
	private CompanyRepository companyRepository;
	
	private MongoCollection<Company> companyCollection;
	
	@Autowired
	private void CompanyServiceImpl() {
		companyCollection = mongoDatabase.getCollection("company").withDocumentClass(Company.class);
	}
	
	@Override
	public List<Company> getAllCompany(int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		List<Company> companies = new ArrayList<>();
		companyCollection
				.find()
				.skip((pageNo -1)*pageSize)
				.limit(pageSize).iterator()
				.forEachRemaining(companies::add);
		return companies;
	}

	@Override
	public Company getCompanyById(String id) {
		// TODO Auto-generated method stub
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Company company = companyCollection.find(filter).first();
		return company;
	}

	@Override
	public boolean updateCompany(Company company) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteCompany(Company company) {
		// TODO Auto-generated method stub
		return false;
	}

// 1. Thống kê có bao nhiêu công ty, số lượng nhân viên của công ty
	@Override
	public Document countCompanyAndTotalEmployee() {
		List<Bson> pipeline = new ArrayList<>();
		
//		facet total Company
		Bson totalCompanyFilter = new Document("$group", new Document("_id", new BsonNull())
				.append("totalCompany", new Document("$sum", 1)));
		
//		facet total Employee in company
		Bson totalEmployeeInCompanyFilter = new Document("$lookup", 
				new Document("from", "employee")
				.append("localField", "_id")
				.append("foreignField", "idCompany")
				.append("as", "employees"));
		Bson totalEmployeeInCompanyProject = new Document("$project", new Document("name", 1)
				.append("toltalEmployeeInCompany", new Document("$size", "$employees")));
		
		List<Bson> queryTotalInCompany = new ArrayList<>();
		queryTotalInCompany.add(totalEmployeeInCompanyFilter);
		queryTotalInCompany.add(totalEmployeeInCompanyProject);
		
//		Facet
		Bson facet = Aggregates.facet(new Facet("totalCompany", totalCompanyFilter), new Facet("totalEmployeeInCompany", queryTotalInCompany));
		
		pipeline.add(facet);
		
		Document document = companyCollection.aggregate(pipeline, Document.class).first();
		return document;
	}

	
// 2. Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức lương phải trả cho những nhân viên đó là bao nhiêu
	@Override
	public Document statisticalCompany(String id, int year) {
		List<Bson> pipeline = new ArrayList<>();
		
		Bson matchCompanyFilter = new Document("$match", new Document("_id", new ObjectId(id)));
		
		Bson lookupEmployee = new Document("$lookup", new Document("from", "employee")
				.append("localField", "_id")
				.append("foreignField", "idCompany")
				.append("as", "employees"));
		
		Bson unwirdEmployees = new Document("$unwind", "$employees");
		
		Bson matchYearJoinEmployee = new Document("$match", 
				new Document("$expr", new Document("$eq", Arrays.asList(new Document("$year", "$employees.timeJoin"), year))));
		
		Bson group = new Document("$group", new Document("_id", "$employees.idCompany")
				.append("data", new Document("$first", "$$ROOT"))
				.append("totalEmployee", new Document("$sum", 1))
				.append("totalSalary", new Document("$sum", "$employees.salary.wage")));
		
		Bson project = new Document("$project", new Document("name","$data.name")
				.append("year", "$data.employees.timeJoin")
				.append("totalEmployee", 1)
				.append("totalSalary", 1));
		
		pipeline.add(matchCompanyFilter);
		pipeline.add(lookupEmployee);
		pipeline.add(unwirdEmployees);
		pipeline.add(matchYearJoinEmployee);
		pipeline.add(group);
		pipeline.add(project);
		
		Document document = companyCollection.aggregate(pipeline, Document.class).first();
		
		return document;
	}
	
//  3. Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm trong năm 2022
	@Override
	public List<Document> statisticalSalaryCompany(int year) {
		List<Bson> pipeline = new ArrayList<>();
		
		Bson lookupEmployee = new Document("$lookup", new Document("from", "employee")
				.append("localField", "_id")
				.append("foreignField", "idCompany")
				.append("as", "employees"));
		
		Bson unwirdEmployees = new Document("$unwind", "$employees");
		
		Bson matchYearJoinEmployee = new Document("$match", 
				new Document("$expr", new Document("$eq", Arrays.asList(new Document("$year", "$employees.timeJoin"), year))));
		
		Bson group = new Document("$group", new Document("_id", "$employees.idCompany")
				.append("data", new Document("$first", "$$ROOT"))
				.append("totalSalary", new Document("$sum", "$employees.salary.wage")));
		
		Bson project = new Document("$project", new Document("name","$data.name")
				.append("year", "$data.employees.timeJoin")
				.append("totalSalary", 1));
		
		pipeline.add(lookupEmployee);
		pipeline.add(unwirdEmployees);
		pipeline.add(matchYearJoinEmployee);
		pipeline.add(group);
		pipeline.add(project);
		List<Document> documents = new ArrayList<>();
		companyCollection.aggregate(pipeline, Document.class).iterator().forEachRemaining(documents::add);
		
		return documents;
	}

// 4. Thống kê tổng số tiền các công ty IT phải trả cho những người đăng ký vào làm trong các năm từ 2020 ~ 2022
	@Override
	public List<Document> statisticalSalaryCompanyOrther(String purpose, int startYear, int endYear) {
		List<Bson> pipeline = new ArrayList<>();
		
		Bson lookupEmployee = new Document("$lookup", new Document("from", "employee")
				.append("localField", "_id")
				.append("foreignField", "idCompany")
				.append("as", "employees"));
		
		Bson unwirdEmployees = new Document("$unwind", "$employees");
		
		Bson matchYearJoinEmployee = new Document("$match", 
				new Document("$expr", new Document("$and", Arrays.asList(
						new Document("$gte", Arrays.asList(new Document("$year", "$employees.timeJoin"),startYear))
						, new Document("$lte", Arrays.asList(new Document("$year", "$employees.timeJoin"),endYear))
						))));
		
		Bson group = new Document("$group", new Document("_id", "$employees.idCompany")
				.append("data", new Document("$first", "$$ROOT"))
				.append("totalSalary", new Document("$sum", "$employees.salary.wage")));
		
//		Phần lọc theo ngành nghề chưa được, cần xem lại
		Bson project = new Document("$project", new Document("name","$data.name")
				.append("purposes", new Document("$filter", new Document("input","$purposes")
						.append("as", "item")
						.append("cond", new Document("$eq", Arrays.asList("$$item.purposes",purpose)))))
				.append("year", "$data.employees.timeJoin")
				.append("totalSalary", 1));
		
		pipeline.add(lookupEmployee);
		pipeline.add(unwirdEmployees);
		pipeline.add(matchYearJoinEmployee);
		pipeline.add(group);
		pipeline.add(project);
		List<Document> documents = new ArrayList<>();
		companyCollection.aggregate(pipeline, Document.class).iterator().forEachRemaining(documents::add);
		
		return documents;
	}

	
}
