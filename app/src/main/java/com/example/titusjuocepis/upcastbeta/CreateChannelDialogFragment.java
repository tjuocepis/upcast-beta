package com.example.titusjuocepis.upcastbeta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by titusjuocepis on 6/3/16.
 */
public class CreateChannelDialogFragment extends DialogFragment {

    private EditText mNameEdit, mTagsEdit;
    private Button redButton, blueButton, greenButton, yellowButton, orangeButton;
    private RadioGroup radioGroup;
    private RadioButton publicButton, protectedButton, privateButton;
    private String pickedColor = "#fa3635";
    private String pickedType = "Public";

    public void setPickedColor(String pickedColor) {
        this.pickedColor = pickedColor;
    }

    private void findButtonViews(View view) {
        redButton = (Button) view.findViewById(R.id.red_button);
        blueButton = (Button) view.findViewById(R.id.blue_button);
        greenButton = (Button) view.findViewById(R.id.green_button);
        yellowButton = (Button) view.findViewById(R.id.yellow_button);
        orangeButton = (Button) view.findViewById(R.id.orange_button);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_buttons);
        publicButton = (RadioButton) view.findViewById(R.id.button_public);
        protectedButton = (RadioButton) view.findViewById(R.id.button_protected);
        privateButton = (RadioButton) view.findViewById(R.id.button_private);
    }

    private void setButtonClickEvents() {
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPickedColor("#fa3635");
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPickedColor("#3b5998");
            }
        });
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPickedColor("#3b9859");
            }
        });
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPickedColor("#ffdd00");
            }
        });
        orangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPickedColor("#ffa500");
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button_public:
                        pickedType = publicButton.getText().toString();
                        break;
                    case R.id.button_protected:
                        pickedType = protectedButton.getText().toString();
                        break;
                    case R.id.button_private:
                        pickedType = privateButton.getText().toString();
                }
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_channel, null);

        findButtonViews(dialogView);
        setButtonClickEvents();

        builder.setView(dialogView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNameEdit = (EditText)((AlertDialog) dialog).findViewById(R.id.nameEditText);
                        String title = mNameEdit.getText().toString();
                        mTagsEdit = (EditText)((AlertDialog) dialog).findViewById(R.id.tagsEditText);
                        String tags = mTagsEdit.getText().toString();
                        listener.onDialogPositiveClick(CreateChannelDialogFragment.this, pickedType, title, pickedColor, tags);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(CreateChannelDialogFragment.this);
                    }
                })
                .setTitle("Create Channel");

        return builder.create();
    }

    public interface CreateChannelDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String type, String title, String color, String tags);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    CreateChannelDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (CreateChannelDialogListener) getTargetFragment();
        } catch(ClassCastException e) {
            e.printStackTrace();
        }
    }
}
