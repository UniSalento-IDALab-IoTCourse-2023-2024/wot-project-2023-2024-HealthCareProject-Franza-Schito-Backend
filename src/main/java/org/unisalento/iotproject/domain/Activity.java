package org.unisalento.iotproject.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("activity")
public class Activity {

    @Id
    private String id;
    private int caloriesOut;
    private int step;
    private int sedentaryMinutes;
    private double distances;
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCaloriesOut() {
        return caloriesOut;
    }

    public void setCaloriesOut(int caloriesOut) {
        this.caloriesOut = caloriesOut;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getSedentaryMinutes() {
        return sedentaryMinutes;
    }

    public void setSedentaryMinutes(int sedentaryMinutes) {
        this.sedentaryMinutes = sedentaryMinutes;
    }

    public double getDistances() {
        return distances;
    }

    public void setDistances(double distances) {
        this.distances = distances;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
