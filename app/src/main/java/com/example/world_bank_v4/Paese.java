package com.example.world_bank_v4;

public class Paese extends MyElementoGenerico {

    /*private String name = null;*/
    /*private String id = null;*/


    public Paese(){}                /*serve per il Gson*/

    public Paese(String name, String id){

        super(id,name);

    }

    @Override
    public String toString() {
        return "Paese [Name = " + super.getName() + " id = " + super.getId()  +" ]";
    }





}

