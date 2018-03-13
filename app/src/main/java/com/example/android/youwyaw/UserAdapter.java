package com.example.android.youwyaw;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arnaud Casamé on 2/25/2018.
 */

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Activity context, ArrayList<User> users){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.user_list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        User currentUser = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView nameTextView = listItemView.findViewById(R.id.member_name);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        nameTextView.setText(currentUser.surname);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView dateTextView = listItemView.findViewById(R.id.member_date);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        dateTextView.setText(currentUser.regDate);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView postTextView = listItemView.findViewById(R.id.member_status);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        postTextView.setText(currentUser.email);

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView stateTextView = listItemView.findViewById(R.id.member_state);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        if(currentUser.state != null && TextUtils.equals(currentUser.state, "Online") ){
            stateTextView.setText(currentUser.state);
            stateTextView.setTextColor( ContextCompat.getColor(getContext(), R.color.colorOnline));
        }else if(currentUser.state != null && TextUtils.equals(currentUser.state, "Offline")){
            stateTextView.setText(currentUser.state);
            stateTextView.setTextColor( ContextCompat.getColor(getContext(), R.color.colorOffline));
        }else{
            stateTextView.setVisibility(View.GONE);
        }


        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        final ImageView userPictureView = listItemView.findViewById(R.id.member_picture);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to iconView
        Picasso.with(getContext())
                .load(currentUser.avatar)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .into(userPictureView, new com.squareup.picasso.Callback() {
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
