package org.unisalento.iotproject.repositories;

import org.unisalento.iotproject.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByCognome(String cognome);

    List<User> findByNomeAndCognome(String nome, String cognome);

    Optional<User> findByEmail(String email);

    User findByRole(String role);

    List<User> findByAddress(String address);
    List<User> findByAddressContaining(String address);
}
