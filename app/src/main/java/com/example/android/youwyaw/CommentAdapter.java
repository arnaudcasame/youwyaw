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
import android.widget.Toast;

import com.example.android.youwyaw.MainActivity;
import com.example.android.youwyaw.Post;
import com.example.android.youwyaw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arnaud Casamé on 2/24/2018.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    public CommentAdapter(Activity context, ArrayList<Comment> comments){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.comment_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Comment currentComment = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView nameTextView = listItemView.findViewById(R.id.comment_item_user_name);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        nameTextView.setText(currentComment.surname);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView dateTextView = listItemView.findViewById(R.id.comment_item_date);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        dateTextView.setText(currentComment.commentDate);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView commentTextView = listItemView.findViewById(R.id.comment_item_text);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        commentTextView.setText(currentComment.commentText);

        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        final ImageView userPictureView = listItemView.findViewById(R.id.comment_item_user_picture);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView

        Picasso.with(getContext())
                .load(currentComment.avatar)
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


        return listItemView;
    }
}
