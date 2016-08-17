package com.example.titusjuocepis.upcastbeta.v2;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titusjuocepis.upcastbeta.BaseChannel;
import com.example.titusjuocepis.upcastbeta.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by titusjuocepis on 8/3/16.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {

    List<Cast> mUsernames;

    public CardViewAdapter(ArrayList<Cast> dataItems) {

        mUsernames = dataItems;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_user_post, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {

        holder.USERNAME = mUsernames.get(position).author;
        holder.msg = mUsernames.get(position).msg;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Card Clicked - " + position, Toast.LENGTH_SHORT).show();
            }
        });

        holder.mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), holder.msg, Toast.LENGTH_SHORT).show();
            }
        });

        holder.mUserName.setText(holder.USERNAME);
        //holder.mMessage.setText(holder.msg);
    }

    @Override
    public int getItemCount() {
        return mUsernames.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView mUserName;
        //TextView mMessage;
        ImageButton mMoreButton;
        //Button mPlayButton;
        String USERNAME;
        String msg;

        public CardViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mUserName = (TextView) itemView.findViewById(R.id.user_post_name);
            //mMessage = (TextView) itemView.findViewById(R.id.card_message);
            mMoreButton = (ImageButton) itemView.findViewById(R.id.user_post_overflow);
            //mPlayButton = (Button) itemView.findViewById(R.id.card_play_button);
        }
    }

    public void updateData(ArrayList<Cast> newData) {
        mUsernames = newData;
        notifyDataSetChanged();
    }
}
