package com.example.world_bank_v4.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.example.world_bank_v4.R;

public class DialogImageMissing extends AppCompatDialogFragment {

    public interface OnClickListener {
        void onFinishClickListener(String inputText);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d(getResources().getString(R.string.APP_NAME), "builder ok");
        builder.setTitle("No image available!")
                .setMessage("Remember that you have the option to save the image at the end of the search")
                .setIcon(R.drawable.warning)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    Resources res = getResources();
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* casting dell'attività che ha passato il fragment */
                        OnClickListener listener = (OnClickListener) getActivity();
                        Log.d(getResources().getString(R.string.APP_NAME), listener.toString());
                        listener.onFinishClickListener(res.getString(R.string.FINISH));

                    }
                });

        return builder.create();

    }
}
