package com.example.world_bank_v4.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import com.example.world_bank_v4.R;

public class DialogContacts extends AppCompatDialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Log.d(res.getString(R.string.APP_NAME),res.getString(R.string.BUILDER_OK));
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.contacts_layout, null));

        builder.setTitle("Contacts")
                .setIcon(R.drawable.logouni1)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }









}




