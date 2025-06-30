package org.unisalento.iotproject.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unisalento.iotproject.domain.Message;
import org.unisalento.iotproject.repositories.MessageRepository;

import java.time.LocalDateTime;


@Component
public class MqttConfig {

    @Autowired
    MessageRepository messageRepository;
    private Mqtt3AsyncClient mqttClient;

    @PostConstruct
    public void init() {
        // Configura il client MQTT
        mqttClient = MqttClient.builder()
                .useMqttVersion3()
                .serverHost("broker.hivemq.com") // Indirizzo del broker MQTT
                .serverPort(1883)
                .buildAsync();

        // Connetti al broker
        mqttClient.connectWith()
                .cleanSession(true)
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Errore nella connessione al broker: " + throwable.getMessage());
                    } else {
                        System.out.println("Connessione al broker avvenuta con successo!");
                    }
                });
    }

    public void subscribeToTopic(String topic) {
        mqttClient.subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(publish -> {
                    try {

                        String payload = new String(publish.getPayloadAsBytes());
                        ObjectMapper objectMapper = new ObjectMapper();
                        Message message = objectMapper.readValue(payload, Message.class);

                        System.out.println("Testo Messaggio: " + message.getMessage());
                        System.out.println("Data: " + message.getDate());
                        System.out.println("Topic: " + message.getTopic());
                        System.out.println("Destinatario: " + message.getDestinatarioId());
                        System.out.println("Mittente: " + message.getMittenteId());

                        messageRepository.save(message);
                    } catch (Exception e) {
                        System.out.println("Errore nella deserializzazione del messaggio: " + e.getMessage());
                    }
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Errore nella sottoscrizione al topic: " + throwable.getMessage());
                    } else {
                        System.out.println("Sottoscritto al topic: " + topic);
                    }
                });
    }

    public void publishMessage(String topic, Message message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(message);

            mqttClient.publishWith()
                    .topic(topic)
                    .payload(jsonMessage.getBytes())
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .send()
                    .whenComplete((pubAck, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Errore nella pubblicazione del messaggio: " + throwable.getMessage());
                        } else {
                            System.out.println("Messaggio pubblicato su topic: " + topic);
                        }
                    });
        } catch (Exception e) {
            System.out.println("Errore nella serializzazione del messaggio: " + e.getMessage());
        }
    }
}
