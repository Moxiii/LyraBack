package com.moxi.lyra.Mongo.Message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMongoRepository extends MongoRepository<MongoMessage, String> {
}
