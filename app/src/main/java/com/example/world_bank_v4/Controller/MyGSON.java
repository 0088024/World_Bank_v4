package com.example.world_bank_v4.Controller;

import android.content.Context;
import android.util.Log;

import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.Model.ElementoGenerico;
import com.example.world_bank_v4.Model.Intestazione;
import com.example.world_bank_v4.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MyGSON {
    Context context;

    public MyGSON(Context context){
        this.context = context;
    }


    /*riceve un file json contenente 1 lista di elementi e istanzia e ritorna
    la rispettiva lista Java*/
    public ArrayList getListFromJson(String file_json, TypeToken typeToken){

         /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di
         elementi dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();

        /* Controlla se il file json presenta un messaggio di invalid format. Es:
                        [

                            {
                                "message": [
                                    {
                                         "id": "175",
                                         "key": "Invalid format",
                                         "value": "The indicator was not found. It may have been deleted or archived."
                                    }
                                ]
                            }

                        ]
        In questo caso infatti la size del array è uguale a 1 */
        if(root.size()==1) {
            Log.d(context.getResources().getString(R.string.NOME_APP), "size_array: " + root.size());
            return null; // non si può proseguire
        }

        JsonElement je2 = root.get(1);

        /* Controlla se l'array non è vuoto, cioè non ci sono dati. Es:
                     [

                        {
                            "page": 0,
                            "pages": 0,
                            "per_page": 0,
                            "total": 0,
                            "sourceid": null,
                            "lastupdated": null
                        },
                        null

                    ]
        In questo caso infatti il secondo elemento del array vale null e quindi non rappresenta a
        sua volta un array */
        if(je2.isJsonArray()==false){
            Log.d(context.getResources().getString(R.string.NOME_APP) , "file json vuoto!");
            return null; // non si può proseguire
        }

        /*DEBUG*/
        JsonArray array_json = je2.getAsJsonArray();   /*qui ho l'array json degl'argomenti*/
        Log.d(context.getResources().getString(R.string.NOME_APP) + " DIM[]", String.valueOf(array_json.size()));

        /*con Gson mappo 1 a 1 gli oggetti del file json in oggetti<T>, i quali sono
        memorizzati in una Lista*/
        Gson gson = new Gson();
        //JsonElement jsonElement = myGSON.getJsonElementList(json_file, 0);
        //ElementoGenerico message = gson.getObjectIntoElement(jsonElement,
                //"message");
        ArrayList list = gson.fromJson(je2, typeToken.getType());

        return list;
    }



    /*riceve un file json contenente 1 lista di elementi e ritorna 1 JsonElemnt che rappresenta
    l'elemento json n° = index -1 */
    public JsonElement getJsonElementList(String file_json, int index){
        /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di
        elementi dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(1);              /*ottengo il jsonElement che corrisponde all'
                                                    array json di oggetti*/

        JsonArray elementi_grafico = je2.getAsJsonArray();  /*ottengo l'array json di oggetti*/
        JsonElement je_primo_elemento_grafico =
                elementi_grafico.get(index);              /*ottengo il jsonElement che corrisponde
                                                          ad 1 oggetto dell'array json di oggetti*/

        return je_primo_elemento_grafico;

    }


    /*istanzia e restituisce un ElementoGenerico corrispondente all'elemento json memberName
    contenuto nell'elemento jsonElement ricevuto*/
    public ElementoGenerico getObjectIntoElement(JsonElement jsonElement, String memberName){

        /*ottengo l'ennesimo elemento all'interno di un elemento json*/
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement element = jsonObject.get(memberName);

        Gson gson = new Gson();
        ElementoGenerico elementoGenerico = gson.fromJson(element, ElementoGenerico.class);

        return elementoGenerico;

    }



    /*ritorna il 1° elemento del file json che se il file è corretto, è l'elemento intestazione*/
    public Intestazione getJsonElementIntestazione(String file_json){

        /*attraverso il parser di Gson ottengo l'elemento che mi interessa, ovvero l'array di
        elementi dello stesso tipo e ordinati in json*/
        JsonElement je = new JsonParser().parse(file_json);
        JsonArray root = je.getAsJsonArray();
        JsonElement je2 = root.get(0);        /*ottengo il jsonElement che corrisponde all'elemento
                                              intestazione*/
        Gson gson = new Gson();
        Intestazione intestazione = gson.fromJson(je2 , Intestazione.class);
        return intestazione;
    }


}
