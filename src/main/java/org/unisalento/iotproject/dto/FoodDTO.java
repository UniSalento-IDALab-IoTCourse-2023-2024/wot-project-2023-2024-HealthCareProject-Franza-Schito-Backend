package org.unisalento.iotproject.dto;

public class FoodDTO {

    private String foodId;
    private int calories;
    private String foodName;
    private int mealTypeId;     //1=Breakfast | 2=Morning Snack | 3=Lunch | 4=Afternoon Snack | 5=Dinner

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getMealTypeId() {
        return mealTypeId;
    }

    public void setMealTypeId(int mealTypeId) {
        this.mealTypeId = mealTypeId;
    }
}

