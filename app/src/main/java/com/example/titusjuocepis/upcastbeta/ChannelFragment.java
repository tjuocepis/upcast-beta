package com.example.titusjuocepis.upcastbeta;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

/**
 * Created by titusjuocepis on 6/9/16.
 */
public class ChannelFragment extends Fragment {

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    private static BaseChannel channel;

    private ArrayList<String> usernames;

    private ListView lv;

    private Firebase channelMemberRef;

    public static ChannelFragment newInstance(BaseChannel chan) {
        ChannelFragment fragment = new ChannelFragment();
        channel = chan;

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usernames = new ArrayList<>();
        channelMemberRef = new Firebase(FIREBASE_URL+"/channel_members/"+channel.getTitle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel, container, false);

        TextView title = (TextView) view.findViewById(R.id.channelTitle);
        TextView tags = (TextView) view.findViewById(R.id.tags);
        TextView users = (TextView) view.findViewById(R.id.subscribers);
        View background = view.findViewById(R.id.item_background);

        title.setText(channel.getTitle());
        tags.setText(channel.getTagsString());
        users.setText(channel.getN_members()+"");
        background.setBackgroundColor(Color.parseColor(channel.getColor()));

        lv = (ListView) view.findViewById(R.id.active_users);

        channelMemberRef.addChildEventListener(childEventListener);

        return view;
    }

    @Override
    public void onPause() {

        super.onPause();

        channelMemberRef.removeEventListener(childEventListener);
        UserManager um = UserManager.getInstance();
        channelMemberRef.child(um.getCurrentUser().getKey()).removeValue();
    }

    public void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, usernames);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            usernames.add(dataSnapshot.getKey());
            updateList();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            usernames.remove(usernames.indexOf(dataSnapshot.getKey()));
            updateList();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
}