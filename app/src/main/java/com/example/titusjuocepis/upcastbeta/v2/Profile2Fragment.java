package com.example.titusjuocepis.upcastbeta.v2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.titusjuocepis.upcastbeta.BaseChannel;
import com.example.titusjuocepis.upcastbeta.ChannelManager;
import com.example.titusjuocepis.upcastbeta.CreateChannelDialogFragment;
import com.example.titusjuocepis.upcastbeta.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile2Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile2Fragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile2Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "username";

    // TODO: Rename and change types of parameters
    private String mUsername;

    private OnFragmentInteractionListener mListener;

    private static Profile2Fragment mInstance = new Profile2Fragment();
    private static boolean ARGS_SET = false;

    private TextView mUsernameText;
    private RecyclerView mRecyclerView;
    private ChannelThumbnailAdapter mChannelAdapter;

    public Profile2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username Parameter 1.
     * @return A new instance of fragment Profile2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile2Fragment getInstance(String username) {

        if (!ARGS_SET) {
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, username);
            mInstance.setArguments(args);
            ARGS_SET = true;
        }

        return mInstance;
    }

    public void updateChannels() {
        ChannelManager cm = ChannelManager.getInstance();
        mChannelAdapter.updateList(cm.getOwnedChannels());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = (View) inflater.inflate(R.layout.fragment_profile2, container, false);

        mUsernameText = (TextView) view.findViewById(R.id.card_profile_label_username);
        mUsernameText.setText(mUsername);

        ChannelManager cm = ChannelManager.getInstance();
        ArrayList<BaseChannel> channelList = cm.getOwnedChannels();

        if (channelList.isEmpty()) {
            TextView noChannelsWarning = new TextView(getContext());
            noChannelsWarning.setText("You are currently not hosting any channels\n\n" +
                    "To create one you can long click the Action button " +
                    "located at the bottom right corner");
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.card_profile_content_layout);
            ll.addView(noChannelsWarning);
        }
        else {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.hosted_channels_recylerview);
            mChannelAdapter = new ChannelThumbnailAdapter(channelList, (Profile2Fragment.OnFragmentInteractionListener) getContext());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mRecyclerView.setAdapter(mChannelAdapter);
        }

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
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChannelClick(String title);
        void onChannelLongClick(String title, View view);
    }
}
