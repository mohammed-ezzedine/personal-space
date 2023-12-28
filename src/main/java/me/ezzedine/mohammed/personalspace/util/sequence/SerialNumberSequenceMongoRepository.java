package me.ezzedine.mohammed.personalspace.util.sequence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SerialNumberSequenceMongoRepository extends MongoRepository<SerialNumberSequence, String> {
}
