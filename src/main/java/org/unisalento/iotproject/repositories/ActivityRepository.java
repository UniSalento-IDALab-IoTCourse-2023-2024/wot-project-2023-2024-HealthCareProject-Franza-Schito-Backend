package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Activity;
import org.unisalento.iotproject.domain.Weight;

import java.util.List;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findByDate(String date);

}