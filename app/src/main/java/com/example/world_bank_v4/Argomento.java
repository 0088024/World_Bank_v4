package com.example.world_bank_v4;

public class Argomento extends MyElementoGenerico {

    private String value = null;
    private String source = null;


    public Argomento(){}                /*serve per il Gson*/

    public Argomento(String id, String name, String value, String source) {

        super(id, null);
        this.value = value;
        this.source = source;

    }


    @Override
    public String toString() {
        return "Argomento [id = " +  super.getId() + "; value = " + value + " ]";
    }


    @Override
    public String getValue() {
        return value;
    }


    @Override
    public String getSource() {
        return source;
    }





}


