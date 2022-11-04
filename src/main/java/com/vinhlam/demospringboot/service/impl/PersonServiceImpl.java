package com.vinhlam.demospringboot.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Document;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.bulk.UpdateRequest;
import com.vinhlam.demospringboot.DTO.PersonDTO;
import com.vinhlam.demospringboot.entity.Person;
import com.vinhlam.demospringboot.entity.personObject.Language;
import com.vinhlam.demospringboot.repository.PersonRepository;
import com.vinhlam.demospringboot.service.PersonService;

@Service
public class PersonServiceImpl implements PersonService{

	@Autowired
	private PersonRepository repo;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<Person> getAll(int pageNo, int pageSize) {
		Query query = new Query();
		query.skip((pageNo - 1)*pageSize).limit(pageSize);
		
		return mongoTemplate.find(query, Person.class);
	}

	@Override
	public PersonDTO getPersonById(String id) {
//		Check object Id
		if(ObjectId.isValid(id)) {
			AggregationOperation match = Aggregation.match(Criteria.where("_id").is(new ObjectId(id)));
	        List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
	        aggregationOperations.add(match);

	        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
	        AggregationResults<Person> groupResults = this.mongoTemplate.aggregate(aggregation, "person", Person.class);

	        Person person = groupResults.getUniqueMappedResult();
	        if(person == null) {
	        	return null;
	        }
	        PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);
	        return personDTO;
		}
		return null;
		
	}

	@Override
	public boolean deletePerson(String id) {
		if(!ObjectId.isValid(id)) {
			return false;
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
		DeleteResult dr = mongoTemplate.remove(query, Person.class);
		return dr.wasAcknowledged();
	}

	@Override
	public Person updatePerson(String id, PersonDTO personDTO) {
//		AggregationOperation match = Aggregation.match(Criteria.where("_id").is(new ObjectId(id)));
//        List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
//        aggregationOperations.add(match);
//
//        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
//        AggregationResults<Person> groupResults = this.mongoTemplate.aggregate(aggregation, "person", Person.class);
//
//        Person newPerson = groupResults.getUniqueMappedResult();
//		
//		newPerson.setFirstName(person.getFirstName());
//		newPerson.setAge(person.getAge());
//		newPerson.setBirthday(person.getBirthday());
//		newPerson.setEmails(person.getEmails());
//		newPerson.setFirstName(person.getFirstName());
//		newPerson.setLanguages(person.getLanguages());
//		newPerson.setInfos(person.getInfos());
//		newPerson.setLastName(person.getLastName());
//		newPerson.setPhones(person.getPhones());
//		newPerson.setSex(person.getSex());
		if(!ObjectId.isValid(id)) {
			return null;
		}
		
		Person person = modelMapper.map(personDTO, Person.class);
		return mongoTemplate.save(person);
	}

	@Override
	public Person insertPerson(PersonDTO personDTO) {
		if(personDTO == null) {
			return null;
		}
		Person person = modelMapper.map(personDTO, Person.class);
		return mongoTemplate.save(person);
		
	}

	@Override
	public boolean addNewLanguage(String id, Language language) {
		if(!ObjectId.isValid(id)) {
			return false;
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
		
		Update update = new Update();
		update.addToSet("languages", language);
		
		UpdateResult ur = mongoTemplate.updateFirst(query, update, Person.class);
		return ur.wasAcknowledged();
	}

}
