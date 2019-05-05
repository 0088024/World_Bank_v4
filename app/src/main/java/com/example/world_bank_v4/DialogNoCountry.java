package com.example.world_bank_v4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

public class DialogNoCountry extends AppCompatDialogFragment {
    private Intent intent;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(Costanti.NOME_APP,"builder ok");
        builder.setTitle("No data available!")
                .setMessage("Want to try another country?")
                .setIcon(R.drawable.missing)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return builder.create();
    }
















}
