package com.vinhlam.demospringboot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vinhlam.demospringboot.entity.Person;

@Repository
public interface PersonRepository extends MongoRepository<Person, String>{

}
