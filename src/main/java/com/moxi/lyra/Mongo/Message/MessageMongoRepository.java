package com.moxi.lyra.Mongo.Message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageMongoRepository extends MongoRepository<MongoMessage, String> {
	@Query("{ 'timestamp' :  {$lt: ?0 } }")
	List<MongoMessage> findByTimestampBefore(LocalDateTime timestamp);
	@Query("{ 'timestamp' :  {$lt: ?0 } }")
	List<MongoMessage> findByTimestampAfter(LocalDateTime timestamp);
}
