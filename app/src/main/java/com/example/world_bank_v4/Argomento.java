package com.example.world_bank_v4;

public class Argomento extends MyElementoGenerico {

    private String value = null;
    private String sourceNote = null;


    public Argomento(){}                /*serve per il Gson*/

    public Argomento(String id, String name, String value, String sourceNote) {

        super(id, null);
        this.value = value;
        this.sourceNote = sourceNote;

    }


    @Override
    public String toString() {
        return "Argomento [id = " +  super.getId() + "; value = " + value + " sourceNote " +
                                                    sourceNote + " ]";
    }


    @Override
    public String getValue() {
        return value;
    }


    @Override
    public String getSourceNote() {
        return sourceNote;
    }





}


