package com.test.abhishek.searchyoutube;

import android.content.Context;
import android.content.Intent;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abhishek on 16-Feb-18.
 */

//Adapter class for RecyclerView of videos
public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.MyViewHolder> {

    private Context mContext;
    private List<VideoItem> mVideoList;

    //class which initialises single view's view
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView thumbnail;
        public TextView video_title, video_id, video_description;
        public RelativeLayout video_view;

        public MyViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
            video_title = (TextView) view.findViewById(R.id.video_title);
            video_id = (TextView) view.findViewById(R.id.video_id);
            video_description = (TextView) view.findViewById(R.id.video_description);
            video_view = (RelativeLayout) view.findViewById(R.id.video_view);
        }
    }

    //constructor for savng the context and the list
    public YoutubeAdapter(Context mContext, List<VideoItem> mVideoList) {
        this.mContext = mContext;
        this.mVideoList = mVideoList;
    }

    //on create method for each view
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);

        return new MyViewHolder(itemView);
    }

    //filling the every item of view with respective text and image
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final VideoItem singleVideo = mVideoList.get(position);
        holder.video_title.setText(singleVideo.getTitle());
        holder.video_id.setText("Video ID : "+singleVideo.getId()+" ");
        holder.video_description.setText(singleVideo.getDescription());

        //placing the thumbnail with picasso library 
        //by resizing it to the size of thumbnail
        Picasso.with(mContext)
                .load(singleVideo.getThumbnailURL())
                .resize(480,270)
                .centerCrop()
                .into(holder.thumbnail);

        //setting on click listener to launch video in new activity
        holder.video_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a intent of player activity class
                Intent intent = new Intent(mContext, PlayerActivity.class);
                //putting extra data for actual layout
                intent.putExtra("VIDEO_ID", singleVideo.getId());
                intent.putExtra("VIDEO_TITLE",singleVideo.getTitle());
                intent.putExtra("VIDEO_DESC",singleVideo.getDescription());

                //adding flag as is required for YoutubePlayerView
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //launching the activity from the saved context
                mContext.startActivity(intent);
            }
        });
    }

    //method to get number of items to be inflated
    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

}