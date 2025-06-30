package org.unisalento.iotproject.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("parameter")
public class Parameter {
    public final static String CITIZEN = "Citizen";
    public final static String REC_MEMBER = "Member";
    public final static String REC_ADMINISTRATOR = "Administrator";

    @Id
    private String id;
    private String battito;
    private String sonno;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBattito() {
        return battito;
    }

    public void setBattito(String battito) {
        this.battito = battito;
    }

    public String getSonno() {
        return sonno;
    }

    public void setSonno(String sonno) {
        this.sonno = sonno;
    }
}

