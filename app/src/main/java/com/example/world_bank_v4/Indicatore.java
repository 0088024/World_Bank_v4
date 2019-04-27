package com.example.world_bank_v4;

public class Indicatore extends MyElementoGenerico {



    public Indicatore(){}                /*serve per il Gson*/



    public Indicatore(String id, String name){

        super(id, name);
    }



    @Override
    public String toString() {
        return "Indicatore [id = " + super.getId() + "; value = " + super.getName() + " ]";
    }




}
