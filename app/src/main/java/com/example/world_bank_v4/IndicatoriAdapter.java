package com.example.world_bank_v4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*classe adattatore per collegare i dati alla lista view e definere il layout all'interno della riga
da visualizzare*/
public class IndicatoriAdapter  extends ArrayAdapter<Indicatore> {

    private ArrayList<Indicatore> list; /*lista di oggetti Argomento implementata con array
                                            ridimensionabile. ArrayList<> implementa interfaccia List<>*/

    public IndicatoriAdapter(Context context, int textViewResourceId, ArrayList<Indicatore> list){
        super(context, textViewResourceId, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
            /*se la vecchia vista non puo' essere riutilizzata carica un nuovo layout per la riga da
            visualizzare*/
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.riga_layout, null);
        }
        /*prendi l'elemento nella posizione specificata alla chiamata di getView()*/
        Indicatore indicatore = list.get(position);
        if (indicatore != null) {
            /*ottieni i riferimenti agli elementi del layout per la riga caricato prima*/
            TextView text_view_Nazione = rowView.findViewById(R.id.text_view_Nazione);
            text_view_Nazione.setText(indicatore.toString());
        }

        return rowView;
    }

}
