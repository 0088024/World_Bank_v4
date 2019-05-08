package com.example.world_bank_v4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

public class VisualizzaDati extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_dati);

        TableRow inflateRow;

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);


        for (int i=0;i<10;i++){

            inflateRow = (TableRow) View.inflate(this, R.layout.table_row, null);
            //set tag for each TableRow
            inflateRow.setTag(i);

            TextView txv = findViewById(R.id.textViewValue);
            txv.setText();
            //add TableRows to TableLayout
            tableLayout.addView(inflateRow);

        }

    }
}
