package com.vinhlam.demospringboot.service.impl;

import java.util.ArrayList;
import java.util.List;

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
import com.vinhlam.demospringboot.entity.Result;
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

	private MongoCollection<Document> personCollection;

	@Autowired
	public void PersonServiceImpl() {
		personCollection = mongoDatabase.getCollection("person");
	}

	@Override
	public ResponseEntity<?> getAll(int pageNo, int pageSize) {
		List<Document> documents = new ArrayList<Document>();
		personCollection.find().skip((pageNo - 1) * pageSize).limit(pageSize).iterator()
				.forEachRemaining(documents::add);

		return ResponseEntity.ok(documents);
	}

	@Override
	public ResponseEntity<?> getPersonById(String id) {
//		Kiểm tra id có phải là ObjectId hay không
		if (!ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id is not ObjectId");
		}

//		Kiểm tra person có tồn tại hay không
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Document document = personCollection.find(filter).first();
		if (document == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person is not exist");
		}

		PersonDTO personDTO = modelMapper.map(document, PersonDTO.class);
		return ResponseEntity.ok(personDTO);
	}

	@Override
	public ResponseEntity<?> deletePerson(String id) {
//		Check id and person exist
		ResponseEntity respon = getPersonById(id);
		if (respon.getStatusCodeValue() != 200) {
			return respon;
		}

		Bson filter = Filters.eq("_id", new ObjectId(id));
		DeleteResult rs = personCollection.deleteOne(filter);

		return rs.wasAcknowledged() ? ResponseEntity.ok("Delete person id: " + id + " success")
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete not success");
	}

	@Override
	public ResponseEntity<?> updatePerson(String id, PersonDTO personDTO) {
//		Check id and person exist
		ResponseEntity respon = getPersonById(id);
		if (respon.getStatusCodeValue() != 200) {
			return respon;
		}

		Person person = modelMapper.map(personDTO, Person.class);
		Person personCheck = mongoTemplate.save(person);

		return personCheck != null ? ResponseEntity.ok("Update person success")
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update person is not success");
	}

	@Override
	public Person insertPerson(PersonDTO personDTO) {
		if (personDTO == null) {
			return null;
		}
		Person person = modelMapper.map(personDTO, Person.class);
		return mongoTemplate.save(person);

	}

	// 2. Viết query update thêm 1 language của 1 person
	@Override
	public ResponseEntity<?> addNewLanguage(String id, Language language) {
//		Check id and person exist
		ResponseEntity respon = getPersonById(id);
		if (respon.getStatusCodeValue() != 200) {
			return respon;
		}
		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
//		Check language đó có tồn tại hay chưa
		Bson languageQuery = Filters.eq("languages.language", language.getLanguage());
//		Kiểm tra xem language đó có tồn tại hay không
		Bson deleteQuery = Filters.eq("languages.language", language.getLanguage());
		Document documentLanguage = personCollection.find(deleteQuery).first();
		if (documentLanguage == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Languague in person don't exist");
		}
		Bson query = Filters.and(filterQuery, deleteQuery);
		Bson updateQuery = Updates.addToSet("languages", language);

		UpdateResult rs = personCollection.updateOne(query, updateQuery);

		return rs.wasAcknowledged() ? ResponseEntity.ok(language)
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update new languages is not success");
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

//3. Viết query xoá 1 language của 1 person
	@Override
	public ResponseEntity<?> deleteLanguage(String id, Language language) {
//		Check id and person exist
		ResponseEntity respon = getPersonById(id);
		if (respon.getStatusCodeValue() != 200) {
			return respon;
		}

		Bson findQuery = Filters.eq("_id", new ObjectId(id));
//		Kiểm tra xem language đó có tồn tại hay không
		Bson deleteQuery = Filters.eq("languages.language", language.getLanguage());
		Document documentLanguage = personCollection.find(deleteQuery).first();
		if (documentLanguage == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Languague in person don't exist");
		}
		Bson query = Filters.and(findQuery, deleteQuery);
		Bson updateQuery = Updates.pull("languages", language);

		UpdateResult rs = personCollection.updateOne(query, updateQuery);

		return rs.wasAcknowledged() ? ResponseEntity.ok(language)
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete languages is not success");

	}

//4. Viết query update thêm 1 info của 1 person
	@Override
	public ResponseEntity<?> addNewInfo(String id, Info info) {
//		Check id and person exist
		ResponseEntity respon = getPersonById(id);
		if (respon.getStatusCodeValue() != 200) {
			return respon;
		}

		Bson filterPerson = Filters.eq("_id", new ObjectId(id));

//		Check info đó có tồn tại hay chưa
		Bson filterInfo = Filters.eq("info.idNo", info.getIdNo());
		Bson query = Filters.and(filterPerson, filterInfo);
		Document documentInfo = personCollection.find(query).first();
		if (documentInfo != null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Info is exist");
		}

		Bson updateBson = Updates.addToSet("info", info);

		UpdateResult ur = personCollection.updateOne(filterPerson, updateBson);

		return ur.wasAcknowledged() ? ResponseEntity.ok("Add new " + info.getIdNo() + " success")
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Add new Info is not success");
	}

//5. Viết query update CMND của 1 user thành deactive (ko còn sử dụng nữa)
	@Override
	public ResponseEntity<?> deacticeCMNDUser(String id, Info info) {
//		Check id and person exist
		ResponseEntity respon = getPersonById(id);
		if (respon.getStatusCodeValue() != 200) {
			return respon;
		}

		Bson filterPerson = Filters.eq("_id", new ObjectId(id));

//		Check info đó có tồn tại hay chưa
		Bson filterInfo = Filters.eq("info.idNo", info.getIdNo());
		Bson query = Filters.and(filterPerson, filterInfo);
		Document documentInfo = personCollection.find(query).first();
		if (documentInfo == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Info is not exist");
		}

		Bson updateQuery = Updates.set("info.status", info.getStatus());
		UpdateResult ur = personCollection.updateMany(query, updateQuery);

		return ur.wasAcknowledged() ? ResponseEntity.ok("Update status info " + info.getIdNo() + " success")
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update status info is not success");
	}

//6. Viết query xoá 1 info của 1 person	
	@Override
	public ResponseEntity<?> deleteInfo(String id, Info info) {
//		Check id and person exist
		ResponseEntity respon = getPersonById(id);
		if (respon.getStatusCodeValue() != 200) {
			return respon;
		}

		Bson filterQuery = Filters.eq("_id", new ObjectId(id));
//		Kiểm tra xem language đó có tồn tại hay không
		Bson deleteQuery = Filters.eq("info.idNo", info.getIdNo());
		Bson query = Filters.and(filterQuery, deleteQuery);
		Document documentLanguage = personCollection.find(query).first();
		if (documentLanguage == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Info in person don't exist");
		}
		Bson updateQuery = Updates.pull("info", info);

		UpdateResult ur = personCollection.updateOne(filterQuery, updateQuery);

		return ur.wasAcknowledged() ? ResponseEntity.ok("Delete info idNo " + info.getIdNo() + " success")
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete info is not success");
	}

//8. Viết query cập nhật giới tính của toàn bộ document trong collection person sang NA (Chưa xác định)
	@Override
	public ResponseEntity<?> updateAllSexToNA() {
//		Person nào giới tính khác chưa xác định thì mới update
		Bson filter = Filters.ne("sex", 2);

		Bson updateQuery = Updates.set("sex", 2);
		UpdateOptions upateOption = new UpdateOptions().upsert(true);
		UpdateResult ur = personCollection.updateMany(filter, updateQuery, upateOption);
		return ur.wasAcknowledged() ? ResponseEntity.ok("Update sex all person is success")
				: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update sex all person is not success");
	}

//9. Viết query đếm trong collection person có bao  nhiêu sdt
	@Override
	public ResponseEntity<?> countTotalPhones() {
		List<Bson> pipeline = new ArrayList<Bson>();
		Bson filterNull = new Document("$match", new Document("phones.phone", new Document("$ne", new BsonNull() )));
		
		Bson project = new Document("$project", new Document("phone_count", new Document("$size", "$phones")));
		Bson group = new Document("$group", new Document("_id", new BsonNull())
				.append("totalPhones", new Document("$sum","$phone_count")));

		pipeline.add(filterNull);
		pipeline.add(project);
		pipeline.add(group);

		Document document = personCollection.aggregate(pipeline, Document.class).first();
		if(document == null) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error");
		}
		return ResponseEntity.ok(document);
	}

//10. Viết query get toàn bộ language hiện có trong collection person (kết quả ko được trùng nhau)
	@Override
	public ResponseEntity<?> getAllLanguageDistinct() {
		List<String> languages = new ArrayList<String>();
		languages = mongoTemplate.findDistinct("languages.language", Person.class, String.class);
		if (languages.size() > 0) {
			return ResponseEntity.ok(languages);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Get All Languages Distinct is not success");
		}
	}
	
// 11. Viết query get những person có tên chứa "Nguyễn" và ngày sinh trong khoảng tháng 2~ tháng 10
	@Override
	public ResponseEntity<?> getPersonByName(String firstName) {
		List<Bson> pipeline = new ArrayList<>();
		Bson set = new Document("$set", new Document("month", new Document("$month", "$birthday")));
		Bson match = new Document("$match", new Document("firstName", new Document("$regex", firstName).append("$options", "i"))
				.append("month", new Document("$gte", 2).append("$lte", 10) ) );
		
		pipeline.add(set);
		pipeline.add(match);
		List<Document> documents = new ArrayList<Document>();
		personCollection.aggregate(pipeline).into(documents);
		if(documents.size() > 0) {
			return ResponseEntity.ok(documents);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not person");
		}
	}
	
	

}
