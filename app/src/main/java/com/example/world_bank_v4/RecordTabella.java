package com.example.world_bank_v4;

import java.util.ArrayList;

public class RecordTabella {

    private long _id;
    private String idPaese;
    private String idIndicatore;
    private String nomePaese;
    private String nomeIndicatore;
    private ArrayList<Grafico> colonne_anni;      /*contiene i valori per ogni anno*/


    public RecordTabella(MyElementoGenerico paese, MyElementoGenerico indicatore,
                         ArrayList<Grafico> lista_grafico){

        this.idPaese = paese.getId();
        this.idIndicatore = indicatore.getId();
        this.nomePaese = paese.getValue();
        this.nomeIndicatore = indicatore.getValue();
        this.colonne_anni = lista_grafico;

    }


    public String getIdPaese(){
        return idPaese;
    }

    public String getIdIndicatore(){
        return  idIndicatore;
    }

    public String getNomePaese(){
        return  nomePaese;
    }

    public String getNomeIndicatore(){
        return  nomeIndicatore;
    }

    public ArrayList<Grafico> getColonne_anni(){
        return  colonne_anni;
    }
}
