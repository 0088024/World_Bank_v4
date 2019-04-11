package com.example.world_bank_v4;

public class Paese {

    private String name = null;
    private String id = null;


    public Paese(){}                /*serve per il Gson*/

    public Paese(String name, String id){

        this.name = name;
        this.id = id;

    }

    @Override
    public String toString() {
        return "Paese [Name = " + name + " id = " + id +" ]";
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}

