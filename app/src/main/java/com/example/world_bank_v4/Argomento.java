package com.example.world_bank_v4;

public class Argomento extends ElementoGenerico {


    private String sourceNote = null;


    public Argomento(){}                /*serve per il Gson*/

    public Argomento(String id, String name, String value, String sourceNote) {

        super(id, null, value);
        this.sourceNote = sourceNote;
    }



    @Override
    public String toString() {
        return "Argomento [id = " +  super.getId() + "; value = " + super.getValue() +
                " sourceNote " + sourceNote + " ]";
    }



    @Override
    public String getSourceNote() {
        return sourceNote;
    }





}


