package com.example.world_bank_v4;

import android.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;


public class DialogDeleteRow extends AppCompatDialogFragment {

    private String scelta_utente;


    public Dialog onCreateDialog(final Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(Costanti.NOME_APP, "builder ok");
        builder.setTitle("Warning!")
                .setIcon(R.drawable.warning)
                .setMessage("Do you really want to delete it?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*CaricaDati.deleterow();*/
                        scelta_utente = "YES";
                    }
                });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.show();

    }


    public String getScelta_utente(){
        return scelta_utente;
    }

}


























