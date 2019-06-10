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

public class DialogWarningData extends AppCompatDialogFragment {

    private Intent intent;


    public interface OnClickListener {
        void onFinishClickListener(String inputText);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(res.getString(R.string.APP_NAME), res.getString(R.string.BUILDER_OK));

        Bundle bundle = getArguments();
        String[] array_stringhe =
                bundle.getStringArray(res.getString(R.string.KEY_ARGUMENTS_DIALOG_CHECK_WARNING_DATA));
        Boolean is_missing_dialog = bundle.getBoolean(res.getString(R.string.KEY_IS_NO_GRAPH_DIALOG));

        builder.setTitle(array_stringhe[0])
                .setMessage(array_stringhe[1])
                .setIcon(bundle.getInt(getResources().getString(R.string.KEY_ID_ICONA)))
                .setPositiveButton(array_stringhe[2], new DialogInterface.OnClickListener() {
                    Resources res = getResources();
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* casting dell'attività che ha passato il fragment */
                        DialogWarningData.OnClickListener listener =
                                (DialogWarningData.OnClickListener) getActivity();
                        if (listener != null) {
                            Log.d(getResources().getString(R.string.APP_NAME), listener.toString());
                        }
                        listener.onFinishClickListener(res.getString(R.string.FINISH));

                    }
                });

        if(is_missing_dialog) {
            builder.setNegativeButton(array_stringhe[3], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }
        return builder.create();

    }
}

