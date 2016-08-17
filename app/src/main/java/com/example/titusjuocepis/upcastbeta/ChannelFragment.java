package com.example.titusjuocepis.upcastbeta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by titusjuocepis on 6/9/16.
 */
public class ChannelFragment extends Fragment {

    public static ChannelFragment newInstance() {
        ChannelFragment fragment = new ChannelFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel, container, false);

        return view;
    }
}
