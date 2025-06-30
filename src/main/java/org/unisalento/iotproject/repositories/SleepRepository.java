package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Sleep;

import java.util.List;

public interface SleepRepository extends MongoRepository<Sleep, String> {
    List<Sleep> findByDate(String date);

}