package com.example.titusjuocepis.upcastbeta;

/**
 * Created by titusjuocepis on 6/3/16.
 */

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BaseChannel} and makes a call to the
 * specified {@link ChannelListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.ViewHolder> {

    private List<BaseChannel> channels;
    private final ChannelListFragment.OnListFragmentInteractionListener mListener;

    public ChannelRecyclerViewAdapter(ArrayList<BaseChannel> items, ChannelListFragment.OnListFragmentInteractionListener listener) {
        channels = new ArrayList<>();
        if (items.isEmpty())
            Log.d("[<<ADAPTER>>] : ", "EMPTY!!!");
        for (BaseChannel c : items) {
            Log.d("[<<ADAPTER>>] : ", c.getTitle() + " " + c.getColor() + " " + c.getTagsString());
            channels.add(c);
        }
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_channels_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = channels.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        //holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentClick(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentLongClick(holder.mItem, holder.mView);
                }
                return true;
            }
        });

        holder.title.setText(holder.mItem.getTitle());
        holder.tags.setText(holder.mItem.getTagsString());
        holder.members.setText(holder.mItem.getN_members()+"");
        holder.layout.setBackgroundColor(Color.parseColor(holder.mItem.getColor()));
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public void addItem(BaseChannel channel) {
        channels.add(channel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView members;
        public final TextView tags;
        public final View layout;
        public BaseChannel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.channel_title);
            members = (TextView) view.findViewById(R.id.channel_members);
            tags = (TextView) view.findViewById(R.id.channel_tags);
            layout = (View) view.findViewById(R.id.channel_layout);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }

    public void addChannel(BaseChannel channel) {
        addItem(channel);
        notifyDataSetChanged();
    }
}