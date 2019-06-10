package com.example.world_bank_v4.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.example.world_bank_v4.R;

public class DialogWarningData extends AppCompatDialogFragment {

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

        return builder.create();

    }
}

