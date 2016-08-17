package com.example.titusjuocepis.upcastbeta.v2;

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
import com.example.titusjuocepis.upcastbeta.DiscoverFragment;
import com.example.titusjuocepis.upcastbeta.ProfileFragment;
import com.example.titusjuocepis.upcastbeta.R;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.firebase.client.Firebase;

public class Main2Activity extends AppCompatActivity implements ChannelListFragment.OnListFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    ImageButton mButtonDiscover;
    ImageButton mButtonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Firebase.setAndroidContext(getApplicationContext());

        mButtonDiscover = (ImageButton) findViewById(R.id.button_discover);

        mButtonDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment discoverFragment = new DiscoverFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_main, discoverFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mButtonProfile = (ImageButton) findViewById(R.id.button_profile);

        mButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment profileFragment = new ProfileFragment();
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
