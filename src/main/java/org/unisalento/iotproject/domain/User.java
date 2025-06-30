package org.unisalento.iotproject.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
public class User {
    public final static String CAREGIVER = "Caregiver";
    public final static String DOCTOR = "Doctor";


    @Id
    private String id;
    private String nome;
    private String cognome;
    private String sex;
    private String email;
    private String city;
    private String address;
    private String birthdate;
    private String password;
    private String telephoneNumber;
    private String role;
    private String caloriesThreshold;
    private String stepsThreshold;
    private String linkedUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCaloriesThreshold() {
        return caloriesThreshold;
    }

    public void setCaloriesThreshold(String caloriesThreshold) {
        this.caloriesThreshold = caloriesThreshold;
    }

    public String getStepsThreshold() {
        return stepsThreshold;
    }

    public void setStepsThreshold(String stepsThreshold) {
        this.stepsThreshold = stepsThreshold;
    }

    public String getLinkedUserId() {
        return linkedUserId;
    }

    public void setLinkedUserId(String linkedUserId) {
        this.linkedUserId = linkedUserId;
    }
}

