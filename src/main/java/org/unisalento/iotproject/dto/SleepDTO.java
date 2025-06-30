package org.unisalento.iotproject.dto;

public class SleepDTO {

    private String id;
    private String duration;
    private int efficiency;
    private int[] stages;
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int[] getStages() {
        return stages;
    }

    public void setStages(int[] stages) {
        this.stages = stages;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

