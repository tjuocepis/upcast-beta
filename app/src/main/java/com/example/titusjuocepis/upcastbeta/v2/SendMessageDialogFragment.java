package com.example.titusjuocepis.upcastbeta.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.titusjuocepis.upcastbeta.R;

/**
 * Created by titusjuocepis on 8/4/16.
 */
public class SendMessageDialogFragment extends DialogFragment {

    EditText mEditMsg;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_send_message, null);

        builder.setView(dialogView)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditMsg = (EditText)((AlertDialog) dialog).findViewById(R.id.edit_text_msg);
                        listener.onDialogPositiveClick(SendMessageDialogFragment.this, mEditMsg.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(SendMessageDialogFragment.this);
                    }
                })
                .setTitle("Send Message");

        return builder.create();
    }

    public interface SendMessageDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String msg);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    SendMessageDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (SendMessageDialogListener) activity;
        } catch(ClassCastException e) {
            e.printStackTrace();
        }
    }
}
