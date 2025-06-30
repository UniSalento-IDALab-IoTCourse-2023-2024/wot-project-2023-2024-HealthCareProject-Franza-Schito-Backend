package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Activity;
import org.unisalento.iotproject.domain.DailyMeal;

import java.util.List;
import java.util.Optional;

public interface DailyMealRepository extends MongoRepository<DailyMeal, String> {
    Optional<DailyMeal> findByDate(String date);
}