package com.example.titusjuocepis.upcastbeta;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.titusjuocepis.upcastbeta.v2.CardViewAdapter;
import com.example.titusjuocepis.upcastbeta.v2.Cast;
import com.example.titusjuocepis.upcastbeta.v2.SendMessageDialogFragment;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class ChannelActivity extends AppCompatActivity implements SendMessageDialogFragment.SendMessageDialogListener {

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    private boolean NAMES_LOADED = false;

    String title, color, tags;
    int nUsers;

    private BaseChannel ChannelContext;

    private ArrayList<String> usernames;

    private ArrayList<Cast> casts;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private CardViewAdapter mCardViewAdapter;
    private RecyclerView mRecyclerView;

    private FloatingActionButton fab;
    String message;

    SendMessageDialogFragment sendMessageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        Intent intent;
        if ((intent = getIntent()) != null) {
            title = intent.getStringExtra("title");
            color = intent.getStringExtra("color");
            tags = intent.getStringExtra("tags");
            nUsers = intent.getIntExtra("n_users", 0);
        }

        ChannelManager cm = ChannelManager.getInstance();

        ChannelContext = cm.getChannel(title);

        usernames = new ArrayList<>();

        TextView titleTV = (TextView) findViewById(R.id.channelTitle);
        TextView tagsTV = (TextView) findViewById(R.id.tags);
        TextView nUsersTV = (TextView) findViewById(R.id.subscribers);
        View bgLayout = (View) findViewById(R.id.item_background);

        titleTV.setText(title);
        tagsTV.setText(tags);
        nUsersTV.setText(nUsers + "");
        bgLayout.setBackgroundColor(Color.parseColor(color));

        casts = new ArrayList<>();
        mCardViewAdapter = new CardViewAdapter(casts);
        mRecyclerView = (RecyclerView) findViewById(R.id.casts_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mCardViewAdapter);

        if (!NAMES_LOADED) {
            LoadCastsFromFirebase loadCastsTask = new LoadCastsFromFirebase();
            loadCastsTask.execute(title);
            NAMES_LOADED = true;
        }

        LoadUsersFromFirebase loadUsersTask = new LoadUsersFromFirebase();
        loadUsersTask.execute(title);

        sendMessageDialog = new SendMessageDialogFragment();

        fab = (FloatingActionButton) findViewById(R.id.fab_send_msg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageDialog.show(getFragmentManager(), "Send Message");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRecyclerView.invalidate();
    }

    private void setUpDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, usernames));
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String msg) {
        message = msg;
        SendMessageTask sendMsgTask = new SendMessageTask();
        sendMsgTask.execute(title);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        sendMessageDialog.dismiss();
    }

    private class LoadUsersFromFirebase extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            final Firebase channelMembersRef = new Firebase(FIREBASE_URL+"/channel_members/"+params[0]);

            channelMembersRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    usernames.add(dataSnapshot.getKey());
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
            setUpDrawer();
        }
    }

    private class LoadCastsFromFirebase extends AsyncTask<String, Void, Void> implements CastsUpdateListener {

        @Override
        protected Void doInBackground(String... params) {

            final Firebase channelCastsRef = new Firebase(FIREBASE_URL+"/channel_casts/"+params[0]);

            channelCastsRef.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Cast cast = new Cast(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                        CastAdded(cast);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getKey();
                    String msg = (String) dataSnapshot.getValue();
                    int i = 0;
                    for (Cast c : casts) {
                        if (c.getAuthor().equals(name)) {
                            casts.remove(i);
                            break;
                        }
                        i++;
                    }
                    mCardViewAdapter.notifyDataSetChanged();
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
        }

        @Override
        public void CastAdded(Cast cast) {
            Log.d("[CASTING] - ", "Data Changed!!! - " + cast.getAuthor());
            casts.add(cast);
            mCardViewAdapter.updateData(casts);
        }
    }

    private interface CastsUpdateListener {

        public void CastAdded(Cast cast);
    }

    private class SendMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            final Firebase channelCastsRef = new Firebase(FIREBASE_URL+"/channel_casts/"+params[0]);

            channelCastsRef.child(UserManager.key()).setValue(message);

            return null;
        }
    }
}
