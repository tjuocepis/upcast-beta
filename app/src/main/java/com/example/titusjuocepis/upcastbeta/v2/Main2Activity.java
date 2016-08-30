package com.example.titusjuocepis.upcastbeta.v2;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titusjuocepis.upcastbeta.BaseChannel;
import com.example.titusjuocepis.upcastbeta.ChannelActivity;
import com.example.titusjuocepis.upcastbeta.ChannelFragment;
import com.example.titusjuocepis.upcastbeta.ChannelListFragment;
import com.example.titusjuocepis.upcastbeta.ChannelManager;
import com.example.titusjuocepis.upcastbeta.CreateChannelDialogFragment;
import com.example.titusjuocepis.upcastbeta.DiscoverFragment;
import com.example.titusjuocepis.upcastbeta.ProfileFragment;
import com.example.titusjuocepis.upcastbeta.R;
import com.example.titusjuocepis.upcastbeta.User;
import com.example.titusjuocepis.upcastbeta.UserManager;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Main2Activity extends AppCompatActivity implements ChannelListFragment.OnListFragmentInteractionListener,
        Profile2Fragment.OnFragmentInteractionListener, CreateChannelDialogFragment.CreateChannelDialogListener {

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    private ImageButton mButtonDiscover, mButtonProfile, mButtonSearch;

    private FloatingActionButton fab;
    private CreateChannelDialogFragment createChannelDialog;
    private UserManager userManager;

    private Profile2Fragment profileFragment;
    private DiscoverFragment discoverFragment;

    private boolean CHANNELS_LOADED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Firebase.setAndroidContext(getApplicationContext());

        userManager = UserManager.getInstance();

        Intent intent;
        if ((intent = getIntent()) != null) {
            if (intent.hasExtra("user")) {
                String email = intent.getStringExtra("user");
                String dateCreated = DateFormat.getDateTimeInstance().format(new Date());
                userManager.addUser(new User(email, dateCreated));
            }
        }

        if (!CHANNELS_LOADED) {
            ChannelManager cm = ChannelManager.getInstance();
            cm.loadChannels();
            CHANNELS_LOADED = true;
        }

        mButtonSearch = (ImageButton) findViewById(R.id.actionbar_search);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverFragment = DiscoverFragment.getInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_main, discoverFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mButtonDiscover = (ImageButton) findViewById(R.id.button_discover);

        mButtonDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mButtonProfile = (ImageButton) findViewById(R.id.button_profile);

        mButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileFragment = Profile2Fragment.getInstance(UserManager.userEmail());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_main, profileFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        createChannelDialog = new CreateChannelDialogFragment();
        //createChannelDialog.setTargetFragment(this, 1);

        fab = (FloatingActionButton) findViewById(R.id.button_camera);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                createChannelDialog.show(getSupportFragmentManager(), "Create Channel");

                return true;
            }
        });

        ArrayList<String> tag = new ArrayList<>();
        tag.add("user");

        BaseChannel channel = new BaseChannel("Public", userManager.getCurrentUser().getKey(),
                "#3b5998", tag, userManager.getCurrentUser().getKey());

        showChannelFragment(channel);
    }

    @Override
    public void onListFragmentClick(BaseChannel item) {

        joinUserToChannelFirebase(item);
        showChannelFragment(item);
    }

    private void joinUserToChannelFirebase(BaseChannel channel) {

        Firebase membersRef = new Firebase(FIREBASE_URL+"/channel_members/"+channel.getTitle());
        UserManager um = UserManager.getInstance();
        membersRef.child(um.getCurrentUser().getKey()).setValue(true);
    }

    private void showChannelFragment(BaseChannel channel) {

        ChannelFragment channelFragment = ChannelFragment.newInstance(channel);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_main, channelFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onListFragmentLongClick(BaseChannel item, View view) {
        Toast.makeText(Main2Activity.this, "Long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChannelClick(String title) {

        Toast.makeText(Main2Activity.this, title, Toast.LENGTH_SHORT).show();
        ChannelManager cm = ChannelManager.getInstance();
        joinUserToChannelFirebase(cm.getOwnedChannel(title));
        showChannelFragment(cm.getOwnedChannel(title));
    }

    @Override
    public void onChannelLongClick(final String title, View view) {
        ImageView iv = (ImageView) view.findViewById(R.id.channel_thumbnail);
        PopupMenu menu = new PopupMenu(Main2Activity.this, iv);
        menu.getMenuInflater().inflate(R.menu.menu_channel_options, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals("Delete")) {
                    ChannelManager cm = ChannelManager.getInstance();
                    cm.deleteChannel(cm.getOwnedChannel(title));
                    profileFragment.updateChannels();
                }

                return true;
            }
        });

        menu.show();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String type, String title, String color, String tags) {
        String[] tagsArray = tags.split("\\s+");
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagsArray));

        BaseChannel channel = new BaseChannel(type, title, color, tagsList, UserManager.userEmail());

        ChannelManager channelManager = ChannelManager.getInstance();
        channelManager.addChannel(channel);

        profileFragment.updateChannels();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    /*
    private class JoinUserToChannelFirebase extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {


            return null;
        }
    }
    */
}
