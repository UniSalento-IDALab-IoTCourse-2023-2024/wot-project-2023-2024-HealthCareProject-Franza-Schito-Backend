package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Food;

import java.util.List;

public interface FoodRepository extends MongoRepository<Food, String> {
    List<Food> findByMealTypeId(int mealTypeId);

}