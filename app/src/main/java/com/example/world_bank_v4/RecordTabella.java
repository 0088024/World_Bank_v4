package com.example.world_bank_v4;

import java.util.ArrayList;



public class RecordTabella {

    private long _id;
    private String idPaese;
    private String idIndicatore;
    private String nomePaese;
    private String nomeIndicatore;
    private String lastUpdated;
    private ArrayList<ValoreGrafico> colonne_anni;      /*contiene i valori per ogni anno*/


    public RecordTabella(Intestazione intestazione, MyElementoGenerico paese,
                         MyElementoGenerico indicatore,
                         ArrayList<ValoreGrafico> lista_Valore_grafico){

        this.idPaese = paese.getId();
        this.idIndicatore = indicatore.getId();
        this.nomePaese = paese.getValue();
        this.nomeIndicatore = indicatore.getValue();
        this.lastUpdated = intestazione.getLastUpdated();
        this.colonne_anni = lista_Valore_grafico;

    }


    public String getIdPaese(){
        return idPaese;
    }

    public String getIdIndicatore(){ return  idIndicatore; }

    public String getNomePaese(){
        return  nomePaese;
    }

    public String getLastUpdated(){
        return  lastUpdated;
    }

    public String getNomeIndicatore(){
        return  nomeIndicatore;
    }


    public ArrayList<ValoreGrafico> getColonne_anni(){
        return  colonne_anni;
    }
}
