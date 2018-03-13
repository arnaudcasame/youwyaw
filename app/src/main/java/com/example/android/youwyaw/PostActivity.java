package com.example.android.youwyaw;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private ImageView commentPostPicture;
    private ImageView commentPostAvatar;
    private TextView commentPostText;
    private TextView commentPostUserName;
    private TextView commentPostDate;
    private ListView commentsList;
    private EditText commentEditText;
    private Button actionComment;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    private String currentPostId;

    private CommentAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final String currentUserId = mAuth.getCurrentUser().getUid();

        String imageUrl;

        Bundle extras = getIntent().getExtras();

        commentPostText = findViewById(R.id.comment_post_text);
        commentPostDate = findViewById(R.id.comment_post_date);
        commentPostUserName = findViewById(R.id.comment_post_user_name);
        commentPostPicture = findViewById(R.id.comment_post_picture_image);
        commentPostAvatar = findViewById(R.id.comment_post_user_picture);
        actionComment = findViewById(R.id.action_comment);
        commentEditText = findViewById(R.id.comment_text);

        commentsList = findViewById(R.id.comments_list);

        if(extras != null) {

            if(extras.getString("post_image_url") != null){
                imageUrl = extras.getString("post_image_url");
                displayImage(imageUrl, commentPostPicture);
            }else{
                commentPostPicture.setVisibility(View.GONE);
            }

            currentPostId = extras.getString("post_id");

            commentPostText.setText(extras.getString("post_text"));
            commentPostDate.setText(extras.getString("post_date"));
            commentPostUserName.setText(extras.getString("post_user_surname"));
            displayImage(extras.getString("post_avatar"), commentPostAvatar);
        }

        actionComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPostId != null && !TextUtils.isEmpty(commentEditText.getText().toString())){
                    actionComment.setEnabled(false);
                    Map comment = new HashMap();
                    comment.put("commentText", commentEditText.getText().toString());
                    comment.put("commentDate", FieldValue.serverTimestamp());
                    comment.put("commentUserId", currentUserId);
                    mStore.collection("posts")
                            .document(currentPostId)
                            .collection("comments")
                            .add(comment)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        commentEditText.setText("");
                                        actionComment.setEnabled(true);
                                        Toast.makeText(PostActivity.this, "Comment Successful", Toast.LENGTH_LONG).show();
                                    }else{
                                        String e = task.getException().getMessage();
                                        Toast.makeText(PostActivity.this, "Error :"+e, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(PostActivity.this, "You must type text before pressing the button", Toast.LENGTH_LONG).show();
                }
            }
        });

        mStore.collection("posts")
                .document(currentPostId)
                .collection("comments")
                .orderBy("commentDate", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Comment> comments = new ArrayList<Comment>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("nono", document.getId() + " => " + document.getData());

                                comments.add(new Comment(
                                        document.getData().get("commentDate").toString(),
                                        document.getData().get("commentUserId").toString(),
                                        document.getData().get("commentText").toString(),
                                        document.getId().toString() ));

                            }
                            itemsAdapter = new CommentAdapter(PostActivity.this, comments);


                            commentsList.setAdapter(itemsAdapter);
                        }
                    }
                });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }else {

        }
    }

    private void displayImage(String url, ImageView pic) {
        // Displays the profile image with the picasso
        Picasso.with(PostActivity.this)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(pic, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
//                        Toast.makeText(PostActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(PostActivity.this, "Profile picture not uploaded", Toast.LENGTH_LONG).show();
                    }
                });
    }


}
