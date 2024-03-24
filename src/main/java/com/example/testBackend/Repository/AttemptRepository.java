package com.example.testBackend.Repository;

import com.example.testBackend.Entities.Attempt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptRepository extends MongoRepository<Attempt, String> {

}