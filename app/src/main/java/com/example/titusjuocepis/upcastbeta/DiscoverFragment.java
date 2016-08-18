package com.example.titusjuocepis.upcastbeta;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by titusjuocepis on 6/3/16.
 */
public class DiscoverFragment extends Fragment {

    private ChannelListFragment channelListFragment;

    private ArrayList<BaseChannel> searchedChannels;

    private EditText searchEdit;

    private boolean FRAGMENT_LOADED = false;

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    private static DiscoverFragment mInstance = new DiscoverFragment();

    public static DiscoverFragment getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        channelListFragment = new ChannelListFragment();
        searchedChannels = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        searchEdit = (EditText) view.findViewById(R.id.search_edit);

        ImageView searchIcon = (ImageView) view.findViewById(R.id.search_button);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchedChannels = new ArrayList<>();

                if (isTagSearch()) {
                    Toast.makeText(getContext(), "Tag Search", Toast.LENGTH_SHORT).show();
                    String[] tokens = searchEdit.getText().toString().split("\\#");
                    SearchTagsFromFirebase searchTagsTask = new SearchTagsFromFirebase();
                    Toast.makeText(getContext(), tokens[1], Toast.LENGTH_SHORT).show();
                    searchTagsTask.execute(tokens[1]);
                }
                else {
                    Toast.makeText(getContext(), "Channel Search", Toast.LENGTH_SHORT).show();
                    SearchChannelsFromFirebase searchChannelsTask = new SearchChannelsFromFirebase();
                    searchChannelsTask.execute(searchEdit.getText().toString());
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        Toast.makeText(getContext(), "RESUMING PAGE", Toast.LENGTH_SHORT).show();
    }

    private boolean isTagSearch() {
        String searchEntry = searchEdit.getText().toString();
        if (searchEntry.startsWith("#"))
            return true;
        return false;
    }

    public void loadFragment() {

        if (FRAGMENT_LOADED) {
            getFragmentManager().beginTransaction().remove(channelListFragment).commit();
        }

        channelListFragment = ChannelListFragment.newInstance(1, searchedChannels);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.search_list_frame, channelListFragment);
        fragmentTransaction.commit();

        FRAGMENT_LOADED = true;
    }

    private class SearchChannelsFromFirebase extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... params) {

            Firebase channelsRef = new Firebase(FIREBASE_URL+"/channels");

            channelsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot.getKey().equals(params[0])) {
                        BaseChannel channel = dataSnapshot.getValue(BaseChannel.class);
                        searchedChannels.add(channel);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadFragment();
        }
    }

    private class SearchTagsFromFirebase extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            Firebase tagsRef = new Firebase(FIREBASE_URL+"/channel_tags/"+params[0]);

            tagsRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for (DataSnapshot snapshot : children) {
                        Log.d("[FIREBASE] : ", " Tag Snapshot Key = " + snapshot.getKey());
                        Firebase channelRef = new Firebase(FIREBASE_URL+"/channels/"+snapshot.getKey());
                        channelRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("[FIREBASE] : ", "Channel Snapshot Key = " + dataSnapshot.getKey());
                                BaseChannel channel = dataSnapshot.getValue(BaseChannel.class);
                                Log.d("[FIREBASE] : ", "Channel Color = " + channel.getColor());
                                searchedChannels.add(channel);
                                loadFragment();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadFragment();
        }
    }
}
