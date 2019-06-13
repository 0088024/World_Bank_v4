package com.example.world_bank_v4.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.example.world_bank_v4.Activities.CaricaDatiActivity;
import com.example.world_bank_v4.Activities.MostraImmagineActivity;
import com.example.world_bank_v4.R;

public class DialogCheckNow extends AppCompatDialogFragment {

    private Intent intent;


    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Resources res = getResources();
        Log.d(res.getString(R.string.APP_NAME), res.getString(R.string.BUILDER_OK));

        Bundle bundle = getArguments();
        String[] array_stringhe =
                bundle.getStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG));
        final String item_menu_selezionato =
                bundle.getString(res.getString(R.string.KEY_ITEM_MENU_SELEZIONATO));
        builder.setTitle(array_stringhe[0])
                .setMessage(array_stringhe[1])
                .setIcon(bundle.getInt(res.getString(R.string.KEY_ID_ICONA)))
                .setPositiveButton(array_stringhe[2], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d(getResources().getString(R.string.APP_NAME), getResources().getString(R.string.ONCLICK_OK));
                        if(item_menu_selezionato.contentEquals(getResources().getString(
                                                            R.string.button_salva_database)))
                            intent = new Intent(getContext(), CaricaDatiActivity.class);
                        else
                            intent = new Intent(getContext(), MostraImmagineActivity.class);

                        startActivity(intent);
                    }
                });

        builder.setNegativeButton(array_stringhe[3], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

}
