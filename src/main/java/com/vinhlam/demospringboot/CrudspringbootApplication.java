package com.vinhlam.demospringboot;

import java.util.concurrent.TimeUnit;

import org.apache.catalina.core.ApplicationContext;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinhlam.demospringboot.entity.Person;

@SpringBootApplication
public class CrudspringbootApplication {
	
	@Value("${spring.data.mongodb.database}")
	private String mongoDB;

	public static void main(String[] args) {
		SpringApplication.run(CrudspringbootApplication.class, args);
	}

	@Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper;
    }
	
//	@Bean
//	public MongoCollection<Document> personCollection() {
//        MongoClient mongoClient = MongoClients.create();
//
//		MongoDatabase db = mongoClient.getDatabase("Person");
//		MongoCollection<Document> personCollection = db.getCollection("person");
//		return personCollection;
//	}
	
	@Bean
	public MongoDatabase mongoDatabase() {
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		return MongoClients.create().getDatabase(mongoDB).withCodecRegistry(pojoCodecRegistry);
	}
	
}
