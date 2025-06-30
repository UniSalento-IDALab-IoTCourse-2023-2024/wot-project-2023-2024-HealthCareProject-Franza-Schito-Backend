package org.unisalento.iotproject.dto;


public class HeartRateDTO {

    private String id;
    private int[] rate;
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRate(int[] rate) {
        this.rate = rate;
    }

    public int[] getRate() {
        return rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
