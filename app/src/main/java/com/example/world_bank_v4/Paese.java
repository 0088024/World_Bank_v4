package com.example.world_bank_v4;

public class Paese {

    private String name = null;

    public Paese(){}                /*serve per il Gson*/

    public Paese(String name){

        this.name = name;
    }

    @Override
    public String toString() {
        return "Paese [Name = " + name + " ]";
    }

    public String getName() {
        return name;
    }
}

