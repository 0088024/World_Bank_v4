package com.example.world_bank_v4.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.world_bank_v4.Activities.ListaPaesiActivity;
import com.example.world_bank_v4.Model.ElementoGenerico;
import com.example.world_bank_v4.R;

import java.util.ArrayList;


/*classe adattatore per collegare i dati contenuti in un ArrayList alla lista view e definere
il layout all'interno della riga da visualizzare*/
public class MyGenericoAdapter extends ArrayAdapter<ElementoGenerico> {

    private Context context;

    private ArrayList<ElementoGenerico> list;          /*lista di oggetti implementata con array ridimensionabile.
                                        ArrayList<> implementa interfaccia List<>*/


    public MyGenericoAdapter(Context context, int textViewResourceId, ArrayList list){
            super(context, textViewResourceId, list);
            this.list = list;
            this.context = context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        /*se la vecchia vista non puo' essere riutilizzata carica un nuovo layout per la riga da
        visualizzare*/
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(context.getClass().getName().contentEquals(ListaPaesiActivity.class.getName()))
                rowView = inflater.inflate(R.layout.riga_layout, null);
            else
                rowView = inflater.inflate(R.layout.riga_layout_indicators, null);

        }
        /*prendi l'elemento nella posizione specificata alla chiamata di getView()*/
        ElementoGenerico elemento = list.get(position);
        if (elemento != null) {
            /*ottieni i riferimenti agli elementi del layout per la riga caricato prima*/
            TextView text_view_elemento = rowView.findViewById(R.id.textView);
            text_view_elemento.setText(elemento.getName());
        }

            return rowView;
    }



    public ArrayList<ElementoGenerico> getList(){
        return list;
    }

}


