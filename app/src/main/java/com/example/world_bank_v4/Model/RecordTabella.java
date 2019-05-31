package com.example.world_bank_v4.Model;

import android.util.Log;

import com.example.world_bank_v4.Model.ElementoGenerico;
import com.example.world_bank_v4.Model.Intestazione;
import com.example.world_bank_v4.Model.ValoreGrafico;

import java.util.ArrayList;



public class RecordTabella {

    private long _id;
    private String localTime;
    private String idPaese;
    private String idIndicatore;
    private String nomePaese;
    private String nomeIndicatore;
    private String lastUpdated;
    private String sourceid;
    private ArrayList<ValoreGrafico> colonne_anni;      /*contiene i valori per ogni anno*/


    public RecordTabella(Intestazione intestazione, String time, ElementoGenerico paese,
                         ElementoGenerico indicatore,
                         ArrayList<ValoreGrafico> lista_Valore_grafico){

        this.localTime = time;
        Log.d(Costanti.NOME_APP, "myStringInRecordTabella: "+localTime);
        this.idPaese = paese.getId();
        this.idIndicatore = indicatore.getId();
        this.nomePaese = paese.getValue();
        this.nomeIndicatore = indicatore.getValue();
        this.lastUpdated = intestazione.getLastUpdated();
        this.sourceid = intestazione.getSourceId();
        this.colonne_anni = lista_Valore_grafico;

    }
    public String getLocalTime(){
        return localTime;
    }

    public String getIdPaese(){
        return idPaese;
    }

    public String getIdIndicatore(){ return  idIndicatore; }

    public String getNomePaese(){
        return  nomePaese;
    }

    public String getNomeIndicatore(){
        return  nomeIndicatore;
    }

    public String getSourceId(){
        return  sourceid;
    }

    public String getLastUpdated(){
        return  lastUpdated;
    }




    public ArrayList<ValoreGrafico> getColonne_anni(){
        return  colonne_anni;
    }
}
