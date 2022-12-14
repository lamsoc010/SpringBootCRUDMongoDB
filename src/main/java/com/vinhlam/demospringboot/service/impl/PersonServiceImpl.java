package com.vinhlam.demospringboot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.bulk.UpdateRequest;
import com.vinhlam.demospringboot.DTO.PersonDTO;
import com.vinhlam.demospringboot.entity.Person;
import com.vinhlam.demospringboot.entity.personObject.Info;
import com.vinhlam.demospringboot.entity.personObject.Language;
import com.vinhlam.demospringboot.repository.PersonRepository;
import com.vinhlam.demospringboot.service.PersonService;

@Service
public class PersonServiceImpl implements PersonService {

	@Autowired
	private PersonRepository repo;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private MongoDatabase mongoDatabase;

	private MongoCollection<Person> personCollection;

	@Autowired
	public void PersonServiceImpl() {
		personCollection = mongoDatabase.getCollection("person").withDocumentClass(Person.class);
	}

	@Override
	public List<PersonDTO> getAll(int pageNo, int pageSize) {
		List<PersonDTO> personDTOs = new ArrayList<PersonDTO>();
		
		List<Bson> pipeline = new ArrayList<>();
		Bson skip = Aggregates.skip((pageNo - 1) * pageSize);
		Bson limit = Aggregates.limit(pageSize);
		pipeline.add(skip);
		pipeline.add(limit);
		personCollection.aggregate(pipeline, PersonDTO.class).into(personDTOs);
		
		return personDTOs;
	}
	

	@Override
	public PersonDTO getPersonById(String id) {

		Bson filter = Filters.eq("_id", new ObjectId(id));
		Person person = personCollection.find(filter).first();
		if(person == null) {
			return null;
		}
		PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);
		
		return personDTO;
	}

	@Override
	public int deletePersonById(String id) {

		Bson filter = Filters.eq("_id", new ObjectId(id));
		DeleteResult rs = personCollection.deleteOne(filter);

		if(rs.getDeletedCount() == 0) {
			return 0; //No success
		} else if(rs.getDeletedCount() >= 1) {
			return 1; //Success
		}
		
		return 2; //Error
	} 

	@Override
	public int updatePersonById(String id, PersonDTO personDTO) {
		
		PersonDTO personDTOCheck = getPersonById(id);
		if(personDTOCheck == null) {
			return 0;
		}
		
		Person person = modelMapper.map(personDTO, Person.class);
		Person personResult = mongoTemplate.save(person);

		if(personResult != null) {
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public boolean insertPerson(PersonDTO personDTO) {
		
		Person person = modelMapper.map(personDTO, Person.class);
		Person personResult = mongoTemplate.save(person);
		if(personResult != null) {
			return true;
		} else {
			return false;
		}

	}

	// 2. Vi???t query update th??m 1 language c???a 1 person
//	@Override
//	public ResponseEntity<?> addNewLanguage(String id, Language language) {
////		Check id and person exist
//		ResponseEntity respon = getPersonById(id);
//		if (respon.getStatusCodeValue() != 200) {
//			return respon;
//		}
//		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
////		Check language ???? c?? t???n t???i hay ch??a
//		Bson languageQuery = Filters.eq("languages.language", language.getLanguage());
////		Ki???m tra xem language ???? c?? t???n t???i hay kh??ng
//		Bson deleteQuery = Filters.eq("languages.language", language.getLanguage());
//		Document documentLanguage = personCollection.find(deleteQuery).first();
//		if (documentLanguage == null) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Languague in person don't exist");
//		}
//		Bson query = Filters.and(filterQuery, deleteQuery);
//		Bson updateQuery = Updates.addToSet("languages", language);
//
//		UpdateResult rs = personCollection.updateOne(query, updateQuery);
//
//		return rs.wasAcknowledged() ? ResponseEntity.ok(language)
//				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update new languages is not success");
//	}
	
//	-----C??u 2 c??ch 2: Vi???t query update th??m 1 language c???a 1 person-------
	@Override
	public int addNewLanguage(String id, Language language) {
		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
		Bson updateLanguageQuery = Updates.addToSet("languages", language);
		
		UpdateResult ur = personCollection.updateOne(filterQuery, updateLanguageQuery);
		
//		Check xem c?? person c?? t???n t???i hay kh??ng
		if(ur.getMatchedCount() == 0) {
			return 0;
		}
//		Check xem c?? tr?????ng n??o ???????c update hay kh??ng
		if (ur.getModifiedCount() > 0) {
			return 1;
		} else { //N???u c?? tr?????ng person t???n t???i nh??ng kh??ng c?? tr?????ng ???????c update m?? s??? d???ng  t???c l?? language ???? ???? t???n t???i
			return 3;
		}
	}

	@Override
	public List<Person> searchPersonByName(String name) {
		Criteria criteria = new Criteria().orOperator((Criteria.where("firstName").regex(name, "i")),
				(Criteria.where("lastName").regex(name, "i")));
		AggregationOperation match = Aggregation.match(criteria);
		List<AggregationOperation> aggregationOperations = new ArrayList<AggregationOperation>();
		aggregationOperations.add(match);

		Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
		AggregationResults<Person> groupResults = this.mongoTemplate.aggregate(aggregation, "person", Person.class);

		List<Person> persons = groupResults.getMappedResults();
		return persons;
	}

//3. Vi???t query xo?? 1 language c???a 1 person
	@Override
	public int deleteLanguage(String id, Language language) {
		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
		Bson deleteLanguageQuery = Updates.pull("languages", language);
		
		UpdateResult ur = personCollection.updateOne(filterQuery, deleteLanguageQuery);
		
//		Check xem c?? person c?? t???n t???i hay kh??ng
		if(ur.getMatchedCount() == 0) {
			return 0;
		}
//		Check xem c?? tr?????ng n??o ???????c update hay kh??ng
		if (ur.getModifiedCount() > 0) {
			return 1;
		} else { //Kh??ng t????n tai
			return 3;
		}

	}

//4. Vi???t query update th??m 1 info c???a 1 person
	@Override
	public int addNewInfo(String id, Info info) {
		
		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
		Bson updateInfosQuery = Updates.addToSet("infos", info);
		
		UpdateResult ur = personCollection.updateOne(filterQuery, updateInfosQuery);
		
//		Check xem c?? person c?? t???n t???i hay kh??ng
		if(ur.getMatchedCount() == 0) {
			return 0;
		}
//		Check xem c?? tr?????ng n??o ???????c update hay kh??ng
		if (ur.getModifiedCount() > 0) {
			return 1;
		} else { //N???u c?? tr?????ng person t???n t???i nh??ng kh??ng c?? tr?????ng ???????c update m?? s??? d???ng  t???c l?? info ???? ???? t???n t???i
			return 3;
		}
	}

//5. Vi???t query update CMND c???a 1 user th??nh deactive (ko c??n s??? d???ng n???a)
	@Override
	public int deacticeCMNDUser(String id, Info info) {

		Bson filterPerson = Filters.eq("_id", new ObjectId(id));
		Bson filterInfo = Filters.eq("infos.idNo", info.getIdNo());
		Bson query = Filters.and(filterPerson, filterInfo);
		
		Bson updateQuery = Updates.set("infos.$[elem].status", info.getStatus());
		UpdateOptions option = new UpdateOptions().arrayFilters(Arrays.asList(
				new Document("elem.type", 1)
				.append("elem.idNo", info.getIdNo())
				.append("elem.status", 1)));
		
		UpdateResult ur = personCollection.updateMany(query, updateQuery, option);
		
//		Check xem c?? person c?? t???n t???i hay kh??ng
		if(ur.getMatchedCount() == 0) {
			return 0;
		}
//		Check xem c?? tr?????ng n??o ???????c update hay kh??ng
		if (ur.getModifiedCount() > 0) {
			return 1;
		} else { 
			return 2;
		}
	}

//6. Vi???t query xo?? 1 info c???a 1 person	
	@Override
	public int deleteInfo(String id, Info info) {
		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
		Bson deleteLanguageQuery = Updates.pull("infos", info);
		
		UpdateResult ur = personCollection.updateOne(filterQuery, deleteLanguageQuery);
		
//		Check xem c?? person c?? t???n t???i hay kh??ng
		if(ur.getMatchedCount() == 0) {
			return 0;
		}
//		Check xem c?? tr?????ng n??o ???????c update hay kh??ng
		if (ur.getModifiedCount() > 0) {
			return 1;
		} else { //Kh??ng t????n la??i
			return 3;
		}
	}

//8. Vi???t query c???p nh???t gi???i t??nh c???a to??n b??? document trong collection person sang NA (Ch??a x??c ?????nh)
	@Override
	public boolean updateAllSexToNA() {
//		Person n??o gi???i t??nh kh??c ch??a x??c ?????nh th?? m???i update
		Bson filter = Filters.ne("sex", 2);

		Bson updateQuery = Updates.set("sex", 2);
		UpdateOptions upateOption = new UpdateOptions().upsert(true);
		UpdateResult ur = personCollection.updateMany(filter, updateQuery, upateOption);
		return ur.wasAcknowledged();
		
	}

//9. Vi???t query ?????m trong collection person c?? bao  nhi??u sdt
	@Override
	public Document countTotalPhones() {
		List<Bson> pipeline = new ArrayList<Bson>();
		Bson filterNull = new Document("$match", new Document("phones.phone", new Document("$ne", new BsonNull() )));
		
		Bson project = new Document("$project", new Document("phone_count", new Document("$size", "$phones")));
		Bson group = new Document("$group", new Document("_id", new BsonNull())
				.append("totalPhones", new Document("$sum","$phone_count")));

		pipeline.add(filterNull);
		pipeline.add(project);
		pipeline.add(group);

		Document document = personCollection.aggregate(pipeline, Document.class).first();
		return document;
		
	}

//10. Vi???t query get to??n b??? language hi???n c?? trong collection person (k???t qu??? ko ???????c tr??ng nhau)
	@Override
	public List<String> getAllLanguageDistinct() {
		List<String> languages = new ArrayList<String>();
		languages = mongoTemplate.findDistinct("languages.language", Person.class, String.class);
		
		return languages;
	}
	
//	S????a ca??ch m????i nh??ng ch??a ????????c, c???? ?????? ??o?? ??a??, xem la??i c??u match, mongo thi?? cha??y ????????c ma?? convert qua java ch??a cha??y ????????c
// 11. Vi???t query get nh???ng person c?? t??n ch???a "Nguy???n" v?? ng??y sinh trong kho???ng th??ng 2~ th??ng 10
	@Override
	public List<PersonDTO> getPersonByName(String name, int startMonth, int endMonth) {
		List<Bson> pipeline = new ArrayList<>();
//		Bson match = new Document("$match", new Document("$or", Arrays.asList(new Document("firstName", new Document("$regex", name).append("$options", "i")
//				.append("lastName", new Document("$regex", name).append("$options", "i"))))) //append of or
//				.append("$expr", new Document("$and", Arrays.asList(new Document("$gte", Arrays.asList(new Document("$month", "$birthday"),startMonth))))
//						.append("$lte", Arrays.asList(new Document("$month", "$birthday"),endMonth))));
		
		Bson match = new Document("$match", new Document("$or", 
				Arrays.asList(new Document("firstName", new Document("$regex", name).append("$options", "i") )
							.append("lastName", new Document("$regex", name).append("$options", "i")))
				).append("$expr", new Document("$and", Arrays.asList(new Document("$gte", Arrays.asList(new Document("$month", "$birthday"), startMonth) )
																	.append("$lte", Arrays.asList(new Document("$month", "$birthday"), endMonth) )  ) ) )  
				);
		pipeline.add(match);
		List<PersonDTO> personDTOs = new ArrayList<PersonDTO>();
		personCollection.aggregate(pipeline, PersonDTO.class).into(personDTOs);
		
		return personDTOs;
	}

	@Override
	public ResponseEntity<?> addNewLanguageAndDeleteInfo(String id, Language language, Info info) {
		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
		Bson updateLanguageQuery = Updates.addToSet("languages", language);
		Bson deleteInfoQuery = Updates.pull("info", info);
		UpdateOptions option = new UpdateOptions().upsert(true);
		
		UpdateResult ur = personCollection.updateOne(filterQuery, updateLanguageQuery, option);
		if(ur.getMatchedCount() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person don't exist");
		}
		if (ur.getModifiedCount() > 0) {
			return ResponseEntity.ok("Update language success");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Language is exist");
		}
	}

	
//	14. //Vi???t query update th??ng tin c???a 1 person, g???m:
//		sex
//		info: th??m m???i 1 info
//		langs: xo?? 1 lang
//		phones: xo?? 1 phone 
	@Override
	public int updateMutilFieldPerson(String id, Person person) {
//		Map<Integer, String> map = new HashMap<Integer,String>();
		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
		
		Bson setUpdate = Updates.set("sex", person.getSex());
		Bson pullUpdate = Updates.pull("languages", new Document("language", person.getLanguages().get(0).getLanguage())
				.append("phones", new Document("phone", person.getPhones().get(0).getPhone())));
		Bson addToSetUpdate = Updates.addToSet("infos", person.getInfos().get(0));
		
		Bson updateDocument = Updates.combine(setUpdate, pullUpdate, addToSetUpdate);

		UpdateResult ur = personCollection.updateOne(filterQuery, updateDocument);
		if(ur.getMatchedCount() == 0) {
			return 0; //Failed
		}
		if (ur.getModifiedCount() > 0) {
			return 1; //Success
		} else {
			return 2; //Failed
		}
	}

	
	

}
