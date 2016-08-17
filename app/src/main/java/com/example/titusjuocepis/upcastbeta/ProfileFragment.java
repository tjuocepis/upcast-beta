package com.example.titusjuocepis.upcastbeta;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements CreateChannelDialogFragment.CreateChannelDialogListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERNAME = "username";

    private String mUsername;

    private OnFragmentInteractionListener mListener;

    private FragmentManager fragmentManager;

    private ChannelListFragment channelListFragment;

    private CreateChannelDialogFragment createChannelDialog;

    private FloatingActionButton createChannelFAB;

    private boolean OWNED_CHANNELS_LOADED;
    private boolean LIST_FRAGMENT_LOADED = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username Profile username.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String username) {

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_USERNAME);
        }

        createChannelDialog = new CreateChannelDialogFragment();
        createChannelDialog.setTargetFragment(this, 1);
    }

    @Override
    public void onStart() {
        super.onStart();

        loadFragment(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "RESUMING PROFILE", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View) inflater.inflate(R.layout.fragment_profile, container, false);
        TextView textView = (TextView) view.findViewById(R.id.username);
        textView.setText(mUsername);

        createChannelFAB = (FloatingActionButton) view.findViewById(R.id.fab_create_channel);
        createChannelFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChannelDialog.show(fragmentManager, "Create Channel");
            }
        });

        final TextView ownedButton = (TextView) view.findViewById(R.id.owned_button);
        final TextView joinedButton = (TextView) view.findViewById(R.id.joined_button);

        ownedButton.setTextColor(Color.parseColor("#fa3635"));
        ownedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OWNED_CHANNELS_LOADED) {
                    loadFragment(true);
                    ownedButton.setTextColor(Color.parseColor("#fa3635"));
                    joinedButton.setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });

        joinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OWNED_CHANNELS_LOADED) {
                    loadFragment(false);
                    joinedButton.setTextColor(Color.parseColor("#fa3635"));
                    ownedButton.setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        createChannelFAB.setOnClickListener(null);
    }

    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, String type, String title, String color, String tags) {

        String[] tagsArray = tags.split("\\s+");
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagsArray));

        BaseChannel channel = new BaseChannel(type, title, color, tagsList, UserManager.userEmail());

        ChannelManager channelManager = ChannelManager.getInstance();
        channelManager.addChannel(channel);

        channelListFragment.getAdapter().addChannel(channel);
    }

    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog) {
        dialog.dismiss();
    }

    private void loadFragment(boolean loadOwned) {
        ChannelManager cm = ChannelManager.getInstance();

        if (LIST_FRAGMENT_LOADED) {
            getFragmentManager().beginTransaction().remove(channelListFragment).commit();
        }

        if (loadOwned) {
            channelListFragment = ChannelListFragment.newInstance(1, cm.getOwnedChannels());
            OWNED_CHANNELS_LOADED = true;
        }
        else {
            channelListFragment = ChannelListFragment.newInstance(1, cm.getJoinedChannels());
            OWNED_CHANNELS_LOADED = false;
        }

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.channels_frame, channelListFragment);
        fragmentTransaction.commit();

        LIST_FRAGMENT_LOADED = true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
