package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Parameter;
import org.unisalento.iotproject.domain.User;

import java.util.List;

public interface ParametersRepository extends MongoRepository<Parameter, String> {
    List<User> findByBattito(String battito);

    List<User> findBySonno(String sonno);

}
