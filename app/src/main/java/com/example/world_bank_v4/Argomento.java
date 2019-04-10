package com.example.world_bank_v4;

public class Argomento {

    private String id = null;
    private String value = null;

    public Argomento(){}                /*serve per il Gson*/

    public Argomento(String id, String value){

        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Argomento [id = " + id + "; value = " + value + " ]";
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

}


