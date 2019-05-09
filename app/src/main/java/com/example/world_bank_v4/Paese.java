package com.example.world_bank_v4;

public class Paese extends ElementoGenerico {


    public Paese(){}                /*serve per il Gson*/

    public Paese(String name, String id, String value){

        super(id,name, value);
    }


    @Override
    public String toString() {
        return "Paese [Name = " + super.getName() + " id = " + super.getId()  +
                " value " + super.getValue() + " ]";
    }



}

