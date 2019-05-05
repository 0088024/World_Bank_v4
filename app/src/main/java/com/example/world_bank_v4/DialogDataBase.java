package com.example.world_bank_v4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.View;

public class DialogDataBase extends AppCompatDialogFragment {

    private Intent intent;

    public Dialog onCreateDialog(Bundle savedInstanceState){
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    Log.d(Costanti.NOME_APP,"builder ok");
    builder.setTitle("Saving completed!")
                    .setMessage("Do you want to check now?")
                    .setIcon(R.drawable.successfull)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    Log.d(Costanti.NOME_APP,"onClick ok");

                    intent = new Intent(getContext(), CaricaDati.class);
                    startActivity(intent);
                    }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                    });

            return builder.create();
    }

}
