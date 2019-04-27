package com.example.world_bank_v4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;



public class ArgomentiAdapter extends MyGenericoAdapter {


    public ArgomentiAdapter(Context context, int textViewResourceId,
                            ArrayList list){
        super(context, textViewResourceId, list);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            /*se la vecchia vista non puo' essere riutilizzata carica un nuovo layout per la riga da
            visualizzare*/
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.riga_layout_argomenti, null);
            }
            /*prendi l'elemento nella posizione specificata alla chiamata di getView()*/
            MyElementoGenerico argomento = super.getList().get(position);
            if (argomento != null) {
                /*ottieni i riferimenti agli elementi del layout per la riga caricato prima*/
                TextView textViewValue = rowView.findViewById(R.id.textViewValue);
                textViewValue.setText(argomento.getValue());

                TextView textViewSourceNote = rowView.findViewById(R.id.textViewSourceNote);
                textViewSourceNote.setText(argomento.getSourceNote());


            }

            return rowView;
        }

    }
