package com.vinhlam.demospringboot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vinhlam.demospringboot.entity.Company;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String>{

}
