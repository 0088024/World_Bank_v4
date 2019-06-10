package com.example.world_bank_v4.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.example.world_bank_v4.Activities.MainActivity;
import com.example.world_bank_v4.R;

public class DialogNoGraph extends AppCompatDialogFragment {

    private Intent intent;

    public interface OnClickListener {
        void onFinishClickListener(String inputText);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(res.getString(R.string.APP_NAME), res.getString(R.string.BUILDER_OK));
        builder.setTitle("No data available!")
                .setMessage("Want to try again?")
                .setIcon(R.drawable.missing)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Resources res = getResources();
                        OnClickListener listener = (OnClickListener) getActivity();
                        Log.d(getResources().getString(R.string.APP_NAME), listener.toString());
                        listener.onFinishClickListener(res.getString(R.string.FINISH));
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
