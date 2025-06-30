package org.unisalento.iotproject.dto;

import java.util.List;

public class DailyMealDTO {
    private String id;
    private String date;
    private List<FoodDTO> foods;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<FoodDTO> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodDTO> foods) {
        this.foods = foods;
    }
}