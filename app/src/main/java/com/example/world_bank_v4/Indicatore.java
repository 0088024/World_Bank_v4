package com.example.world_bank_v4;

public class Indicatore {

    private String id = null;
    private String name = null;

    public Indicatore(){}                /*serve per il Gson*/

    public Indicatore(String id, String name){

        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Indicatore [id = " + id + "; value = " + name + " ]";
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
