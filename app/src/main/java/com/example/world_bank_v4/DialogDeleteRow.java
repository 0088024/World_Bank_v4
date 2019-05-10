package com.example.world_bank_v4;

import android.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.View;


public class DialogDeleteRow extends AppCompatDialogFragment{

    public interface OnClickListener {
        void onFinishClickListener(String inputText);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(Costanti.NOME_APP, "builder ok");
        builder.setTitle("Warning!")
                .setIcon(R.drawable.warning)
                .setMessage("Do you really want to delete it?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnClickListener listener = (OnClickListener) getActivity();
                        listener.onFinishClickListener("delete");
                        DialogDeleteRow.this.dismiss();
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


























