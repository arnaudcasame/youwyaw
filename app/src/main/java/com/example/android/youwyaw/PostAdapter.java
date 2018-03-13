package com.example.android.youwyaw;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.youwyaw.MainActivity;
import com.example.android.youwyaw.Post;
import com.example.android.youwyaw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arnaud Casamé on 2/24/2018.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    public PostAdapter(Activity context, ArrayList<Post> posts){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, posts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_list, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Post currentPost = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView nameTextView = listItemView.findViewById(R.id.user_name);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        nameTextView.setText(currentPost.surname);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.post_date);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        dateTextView.setText(currentPost.postDate);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView postTextView = listItemView.findViewById(R.id.post_text);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        postTextView.setText(currentPost.postText);

        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        final ImageView userPictureView = listItemView.findViewById(R.id.user_picture);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView
        Picasso.with(getContext())
                .load(currentPost.avatar)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .into(userPictureView, new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess() {
                        userPictureView.requestLayout();
                    }

                    @Override
                    public void onError() {
                        userPictureView.requestLayout();
                    }
                });

        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        ImageView postPictureView = listItemView.findViewById(R.id.post_picture_image);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView
        if(currentPost.hasImage()){
            Picasso.with(getContext())
                    .load(currentPost.postImageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(postPictureView, new com.squareup.picasso.Callback(){
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
            postPictureView.setVisibility(View.VISIBLE);
        }else{
            postPictureView.setVisibility(View.GONE);
        }


        return listItemView;
    }
}
