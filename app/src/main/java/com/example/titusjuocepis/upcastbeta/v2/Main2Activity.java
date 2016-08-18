package com.example.titusjuocepis.upcastbeta.v2;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.titusjuocepis.upcastbeta.BaseChannel;
import com.example.titusjuocepis.upcastbeta.ChannelListFragment;
import com.example.titusjuocepis.upcastbeta.ChannelManager;
import com.example.titusjuocepis.upcastbeta.DiscoverFragment;
import com.example.titusjuocepis.upcastbeta.ProfileFragment;
import com.example.titusjuocepis.upcastbeta.R;
import com.example.titusjuocepis.upcastbeta.User;
import com.example.titusjuocepis.upcastbeta.UserManager;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.firebase.client.Firebase;

import java.text.DateFormat;
import java.util.Date;

public class Main2Activity extends AppCompatActivity implements ChannelListFragment.OnListFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    private ImageButton mButtonDiscover, mButtonProfile, mButtonSearch;

    private Fragment discoverFragment, profileFragment;

    private UserManager userManager;

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
                DiscoverFragment discoverFragment = DiscoverFragment.getInstance();
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
                ProfileFragment profileFragment = ProfileFragment.getInstance(UserManager.userEmail());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_main, profileFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public void onListFragmentClick(BaseChannel item) {
        Toast.makeText(Main2Activity.this, "Click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListFragmentLongClick(BaseChannel item, View view) {
        Toast.makeText(Main2Activity.this, "Long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(Main2Activity.this, "Poop!", Toast.LENGTH_SHORT).show();
    }
}
