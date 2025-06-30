package org.unisalento.iotproject.dto;

import java.util.List;

public class HeartRateDTO2 {

    private String idCaregiver;
    private String date;
    private List<Integer> maxRates;
    private List<Integer> minRates;

    public String getIdCaregiver() {
        return idCaregiver;
    }

    public void setIdCaregiver(String idCaregiver) {
        this.idCaregiver = idCaregiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getMaxRates() {
        return maxRates;
    }

    public void setMaxRates(List<Integer> maxRates) {
        this.maxRates = maxRates;
    }

    public List<Integer> getMinRates() {
        return minRates;
    }

    public void setMinRates(List<Integer> minRates) {
        this.minRates = minRates;
    }
}
