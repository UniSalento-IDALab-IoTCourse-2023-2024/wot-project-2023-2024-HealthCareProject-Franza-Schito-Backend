package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.HeartRate;

import java.util.List;

public interface HeartRateRepository  extends MongoRepository<HeartRate, String>  {
    List<HeartRate> findByDate(String date);

}
