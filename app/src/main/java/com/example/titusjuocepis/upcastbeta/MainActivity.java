package com.example.titusjuocepis.upcastbeta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ChannelListFragment.OnListFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    private boolean CHANNELS_LOADED = false;

    private TabLayout mTabLayout;

    private int currentTab = 0;

    private int[] mTabsIcons = {
            R.drawable.ic_earth_512,
            R.drawable.ic_camera_512,
            R.drawable.ic_user_512};

    private UserManager userManager = UserManager.getInstance();

    private BaseChannel chosenChannel;

    private MyPagerAdapter pagerAdapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        // Setup the viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        if (viewPager != null)
            viewPager.setAdapter(pagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(pagerAdapter.getTabView(i));
            }

            //mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }

        Firebase.setAndroidContext(getApplicationContext());
        Firebase ref = new Firebase(FIREBASE_URL+"/channels");
        ref.keepSynced(true);

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

        //startService(new Intent(MainActivity.this, FirebaseBackgroundService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(MainActivity.this, "Main Activity started!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onListFragmentClick(BaseChannel item) {
        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("color", item.getColor());
        intent.putExtra("tags", item.getTagsString());
        intent.putExtra("n_users", item.getN_members());
        startActivity(intent);
        //pagerAdapter.getItem(currentTab).getFragmentManager().beginTransaction().attach(ChannelFragment.newInstance()).commit();
    }

    @Override
    public void onListFragmentLongClick(BaseChannel item, View view) {
        registerForContextMenu(view);
        openContextMenu(view);
        Toast.makeText(MainActivity.this, "Long: "+item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        TextView  titleView = (TextView) v.findViewById(R.id.channel_title);
        String title = titleView.getText().toString();

        ChannelManager cm = ChannelManager.getInstance();

        chosenChannel = cm.getChannel(title);

        menu.setHeaderTitle(title);
        menu.add(Menu.NONE, 1, Menu.NONE, "Join");
        menu.add(Menu.NONE, 2, Menu.NONE, "Rules");
        menu.add(Menu.NONE, 3, Menu.NONE, "Back");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case 1:
                Toast.makeText(MainActivity.this, "Join!", Toast.LENGTH_SHORT).show();
                JoinChannelFirebase joinChannelTask = new JoinChannelFirebase();
                joinChannelTask.execute(chosenChannel.getTitle());
                return true;
            case 2:
                Toast.makeText(MainActivity.this, "Rules!", Toast.LENGTH_SHORT).show();
                return true;
            case 3:
                Toast.makeText(MainActivity.this, "Back!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 3;

        private final String[] mTabsTitle = {"Discover", "Capture", "Profile"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(mTabsTitle[position]);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(mTabsIcons[position]);
            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    currentTab = 0;
                    return DiscoverFragment.getInstance();
                case 1:
                    currentTab = 1;
                    return TestFragment.newInstance();
                case 2:
                    currentTab = 2;
                    return ProfileFragment.getInstance(UserManager.userEmail());

            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }



    /*
    *
    *
     */

    private class JoinChannelFirebase extends AsyncTask<String, Void, Void> {

        String result;

        @Override
        protected Void doInBackground(String... params) {

            final Firebase channelMembersRef = new Firebase(FIREBASE_URL+"/channel_members/"+params[0]);
            final Firebase channelRef = new Firebase(FIREBASE_URL+"/channels/"+params[0]);

            channelMembersRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("[FIREBASE] : ", dataSnapshot.getKey());
                    if (!(dataSnapshot.child(UserManager.key()).exists())) {
                        Log.d("[FIREBASE] : ", channelMembersRef.getKey());
                        Log.d("[FIREBASE] : ", channelMembersRef.child(UserManager.key()).getKey());
                        channelMembersRef.child(UserManager.key()).setValue(true);
                        BaseChannel channel = dataSnapshot.getValue(BaseChannel.class);
                        channel.upMemberCount();
                        channel.upMemberCount();
                        channelRef.child("n_members").setValue(channel.getN_members());
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You are already a member!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return null;
        }
    }
}
