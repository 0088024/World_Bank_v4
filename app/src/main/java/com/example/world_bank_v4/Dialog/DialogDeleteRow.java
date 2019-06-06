package com.example.world_bank_v4.Dialog;

import android.app.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.R;


public class DialogDeleteRow extends AppCompatDialogFragment{

    public interface OnClickListener {
        void onFinishClickListener(String inputText);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(getResources().getString(R.string.NOME_APP), "builder ok");
        builder.setTitle("Warning!")
                .setIcon(R.drawable.warning)
                .setMessage("Do you really want to delete it?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* casting dell'attività che ha passato il fragment */
                        OnClickListener listener = (OnClickListener) getActivity();
                        Log.d(getResources().getString(R.string.NOME_APP), listener.toString());
                        listener.onFinishClickListener("delete");
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


























