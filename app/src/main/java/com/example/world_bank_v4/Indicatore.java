package com.example.world_bank_v4;

public class Indicatore extends MyElementoGenerico {


    public Indicatore(){}                /*serve per il Gson*/



    public Indicatore(String id, String name, String value){

        super(id, name, value);
    }



    @Override
    public String toString() {
        return "Indicatore [id = " + super.getId() + "; name = " + super.getName() +
                 " value " + super.getValue() + " ]";
    }



}
