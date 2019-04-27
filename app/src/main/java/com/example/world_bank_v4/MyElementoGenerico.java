package com.example.world_bank_v4;

public class MyElementoGenerico {

    private String id = null;
    private String name = null;


    public MyElementoGenerico()  {}  /*serve x il GSON*/

    public MyElementoGenerico(String id, String name){

        this.id = id;
        this.name = name;

    }



    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getSourceNote(){
        return "Metodo della superclasse MyElementoGenerico";
    }


    public String getValue(){
        return "Metodo della superclasse MyElementoGenerico";
    }
}
