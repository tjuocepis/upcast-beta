package com.example.titusjuocepis.upcastbeta.v2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.titusjuocepis.upcastbeta.BaseChannel;
import com.example.titusjuocepis.upcastbeta.R;
import com.fasterxml.jackson.databind.deser.Deserializers;

import java.util.ArrayList;

/**
 * Created by titusjuocepis on 8/18/16.
 */
public class ChannelThumbnailAdapter extends RecyclerView.Adapter<ChannelThumbnailAdapter.ViewHolder> {

    private ArrayList<BaseChannel> chanList;
    private Profile2Fragment.OnFragmentInteractionListener mListener;

    public ChannelThumbnailAdapter(ArrayList<BaseChannel> list, Profile2Fragment.OnFragmentInteractionListener listener) {
        chanList = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final String title = chanList.get(position).getTitle();
        holder.mChannelTitle.setText(title);

        String color = chanList.get(position).getColor();
        holder.mChannelThumbnail.setBackgroundColor(Color.parseColor(color));
        holder.mChannelThumbnail.setImageBitmap(null);

        holder.mChannelThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onChannelClick(title);
            }
        });

        holder.mChannelThumbnail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onChannelLongClick(title, holder.mView);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return chanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView mChannelTitle;
        RoundedCorners mChannelThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mChannelTitle = (TextView) itemView.findViewById(R.id.channel_thumbnail_title);
            mChannelThumbnail = (RoundedCorners) itemView.findViewById(R.id.channel_thumbnail);
        }
    }
}
