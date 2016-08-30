package com.example.titusjuocepis.upcastbeta;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;

/**
 * Created by titusjuocepis on 6/3/16.
 */
public class ChannelManager {

    private static ChannelManager ourInstance = new ChannelManager();

    public static ChannelManager getInstance() {
        return ourInstance;
    }

    private ArrayList<BaseChannel> ownedChannels;
    private ArrayList<BaseChannel> joinedChannels;

    private LoadChannelsFromFirebase loadChannelsTask;
    private AddChannelToFirebase addChannelTask;
    private DeleteChannelFromFirebase deleteChannelTask;

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    private ChannelManager() {
        ownedChannels = new ArrayList<>();
        joinedChannels = new ArrayList<>();
    }

    public void loadChannels() {
        loadChannelsTask = new LoadChannelsFromFirebase();
        loadChannelsTask.execute();
    }

    public ArrayList<BaseChannel> getOwnedChannels() {
        return ownedChannels;
    }

    public BaseChannel getOwnedChannel(String title) {
        for (BaseChannel c : ownedChannels) {
            if (c.getTitle().equals(title))
                return c;
        }

        return null;
    }

    public ArrayList<BaseChannel> getJoinedChannels() {
        return joinedChannels;
    }

    public BaseChannel getJoinedChannel(String title) {
        for (BaseChannel c : joinedChannels) {
            if (c.getTitle().equals(title))
                return c;
        }

        return null;
    }

    public BaseChannel getChannel(String title) {
        BaseChannel channel = getOwnedChannel(title);
        if (channel == null)
            channel = getJoinedChannel(title);
        return channel;
    }

    public void addChannel(BaseChannel channel) {
        addChannelTask = new AddChannelToFirebase();
        addChannelTask.execute(channel);
        ownedChannels.add(channel);
    }

    public void deleteChannel(BaseChannel channel) {
        deleteChannelTask = new DeleteChannelFromFirebase();
        deleteChannelTask.execute(channel);
        ownedChannels.remove(channel);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.child("host").child(UserManager.key()).exists()) {

                BaseChannel channel = dataSnapshot.getValue(BaseChannel.class);
                ownedChannels.add(channel);
                Log.d("[FIREBASE] : ", "Channel Title - " + channel.getTitle());
                Log.d("[FIREBASE] : ", "Current User - " + UserManager.userEmail());
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
    };

    private class LoadChannelsFromFirebase extends AsyncTask<Void,Void,ArrayList<BaseChannel>> {

        private ArrayList<String> tags = new ArrayList<>();

        public LoadChannelsFromFirebase() {
            tags.add("tag1");
            tags.add("tag2");
            tags.add("tag3");
        }

        @Override
        protected ArrayList<BaseChannel> doInBackground(Void... params) {

            final ArrayList<BaseChannel> result = new ArrayList<>();

            Firebase ref = new Firebase(FIREBASE_URL+"/channels");

            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot.child("host").child(UserManager.key()).exists()) {

                        BaseChannel channel = dataSnapshot.getValue(BaseChannel.class);
                        result.add(channel);
                        Log.d("[FIREBASE] : ", "Channel Title - " + channel.getTitle());
                        Log.d("[FIREBASE] : ", "Current User - " + UserManager.userEmail());

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
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<BaseChannel> result) {
            super.onPostExecute(result);
            ownedChannels = result;
        }
    }

    private class AddChannelToFirebase extends AsyncTask<BaseChannel, Void, BaseChannel> {

        @Override
        protected BaseChannel doInBackground(BaseChannel... params) {
            BaseChannel channel = params[0];

            Firebase root = new Firebase(FIREBASE_URL);
            Firebase ref = new Firebase(FIREBASE_URL+"/channels");
            Firebase channelMembersRef = new Firebase(FIREBASE_URL+"/channel_members");
            Firebase tagsRef = new Firebase(FIREBASE_URL+"/channel_tags");

            ref.child(channel.getTitle()).setValue(channel);
            root.child("/channels/" + channel.getTitle() + "/host/" + UserManager.key()).setValue(true);
            channelMembersRef.child(channel.getTitle()).child(UserManager.key()).setValue(true);

            for(String tag : channel.getTags()) {
                tagsRef.child(tag).child(channel.getTitle()).setValue(true);
            }

            return channel;
        }

        @Override
        protected void onPostExecute(BaseChannel channel) {
            super.onPostExecute(channel);
            Log.d("[FIREBASE] : ", "Finished adding channel!");
        }
    }

    private class DeleteChannelFromFirebase extends AsyncTask<BaseChannel, Void, String> {

        @Override
        protected String doInBackground(BaseChannel... params) {

            BaseChannel channel = params[0];
            String channelTitle = channel.getTitle();

            Firebase castsRef = new Firebase(FIREBASE_URL+"/channel_casts");
            castsRef.child(channelTitle).removeValue();

            Firebase membersRef = new Firebase(FIREBASE_URL+"/channel_members");
            membersRef.child(channelTitle).removeValue();

            Firebase tagsRef = new Firebase(FIREBASE_URL+"/channel_tags");
            for (String tag : channel.getTags()) {
                tagsRef.child(tag).child(channelTitle).removeValue();
            }

            Firebase channelsRef = new Firebase(FIREBASE_URL+"/channels");
            channelsRef.child(channelTitle).removeValue();

            return channelTitle;
        }
    }
}
