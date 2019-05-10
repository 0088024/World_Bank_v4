package com.example.world_bank_v4;

public class ElementoGenerico {

    private String id = null;
    private String name = null;
    private String value = null;


    public ElementoGenerico()  {}  /*serve x il GSON*/

    public ElementoGenerico(String id, String name, String value ){

        this.id = id;
        this.name = name;
        this.value = value;
    }



    public String getId() {
        return id;
    }


    public String getName() {return name; }


    public String getSourceNote(){
        return "Metodo della superclasse ElementoGenerico";
    }


    public String getValue(){
        return value;
    }
}
