package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Parameter;
import org.unisalento.iotproject.domain.Spo2;
import org.unisalento.iotproject.domain.User;

import java.util.List;

public interface Spo2Repository extends MongoRepository<Spo2, String> {
    List<Spo2> findByDate(String date);

    List<Spo2> findByMin(String min);
    List<Spo2> findByMax(String max);
    List<Spo2> findByMedia(String media);

}