package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Spo2;
import org.unisalento.iotproject.domain.Weight;

import java.util.List;

public interface WeightRepository extends MongoRepository<Weight, String> {
    List<Weight> findByDate(String date);

}