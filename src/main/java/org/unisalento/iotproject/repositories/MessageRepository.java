package org.unisalento.iotproject.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.unisalento.iotproject.domain.Message;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByDestinatarioId(String destinatarioId);
    List<Message> findByMittenteId(String mittenteId);
    List<Message> findByDestinatarioIdAndMittenteId(String destinatarioId, String mittenteId);
    List<Message> findByDate(String date);
    List<Message> findByTopic(String topic);

}