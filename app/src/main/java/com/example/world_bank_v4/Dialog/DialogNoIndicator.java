package com.example.world_bank_v4.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.example.world_bank_v4.Activities.MainActivity;
import com.example.world_bank_v4.Model.Costanti;
import com.example.world_bank_v4.R;

public class DialogNoIndicator extends AppCompatDialogFragment {

    private Intent intent;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(getResources().getString(R.string.NOME_APP),"builder ok");
        builder.setTitle("No data available!")
                .setMessage("Want to try another indicator?")
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
