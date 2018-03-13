package com.example.android.youwyaw;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static String TAG = "nono";

    private String mUserId;
    private DatabaseReference mDatabase;
    private FirebaseFirestore mFirestore;

    public Map actualUser;

    private FloatingActionButton addPostButton;

    private PostAdapter itemsAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        addPostButton = findViewById(R.id.add_post_button);
        listView = findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post currentPost = itemsAdapter.getItem(i);
                Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
                postIntent.putExtra("post_id", currentPost.postId);
                postIntent.putExtra("post_avatar", currentPost.avatar);
                postIntent.putExtra("post_user_surname", currentPost.surname);
                postIntent.putExtra("post_date", currentPost.postDate);
                postIntent.putExtra("post_text", currentPost.postText);
                if(currentPost.postImageUrl != null){
                    postIntent.putExtra("post_image_url", currentPost.postImageUrl);
                }
                startActivity(postIntent);
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPost = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(addPost);
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if(currentUser == null){
            sendToLogin();
        }else{
            mUserId = currentUser.getUid();

            mFirestore
                    .collection("users")
                    .document(mUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(!documentSnapshot.exists()){
                                DatabaseReference userRef = mDatabase.child("users").child(mUserId).getRef();
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        mFirestore
                                                .collection("users")
                                                .document(mUserId)
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mFirestore
                                                                .collection("users")
                                                                .document(mUserId).update("regDate", FieldValue.serverTimestamp());
                                                        mFirestore
                                                                .collection("users")
                                                                .document(mUserId).update("state", "Online");
                                                        Toast.makeText(MainActivity.this, "Transfert effectué", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }else{
                                mFirestore
                                        .collection("users")
                                        .document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){

                                            if(task.getResult().getData().get("avatar") == null){
                                                Toast.makeText(MainActivity.this, "You must have a profile picture", Toast.LENGTH_LONG).show();
                                                Intent settingIntent = new Intent(MainActivity.this, ProfileActivity.class);
                                                startActivity(settingIntent);
                                                finish();
                                            }else if(task.getResult().getData().get("surname") == null){
                                                Toast.makeText(MainActivity.this, "You must have a surname", Toast.LENGTH_LONG).show();
                                                Intent settingIntent = new Intent(MainActivity.this, ProfileActivity.class);
                                                startActivity(settingIntent);
                                                finish();
                                            }else{
                                                mFirestore
                                                        .collection("users")
                                                        .document(mUserId).update("state", "Online");
                                                actualUser = task.getResult().getData();
                                                Log.i(TAG, task.getResult().getData().toString());
                                            }
                                        }else{
                                            Log.w(TAG, "Error getting documents.", task.getException());

                                        }

                                    }
                                });
                            }
                        }
                    });




            mFirestore
                    .collection("posts")
                    .orderBy("postDate", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                final ArrayList<Comment> comments = new ArrayList<Comment>();
                                ArrayList<Post> posts = new ArrayList<Post>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    if(document.getData().get("postImageUrl") != null){
                                        posts.add(new Post(
                                                document.getData().get("postDate").toString(),
                                                document.getData().get("postUserId").toString(),
                                                document.getData().get("postText").toString(),
                                                document.getData().get("postImageUrl").toString(),
                                                document.getId().toString() ));

                                    }else if(document.getData().get("postDate") != null &&
                                            document.getData().get("postUserId") != null &&
                                            document.getData().get("postText") != null){
                                        posts.add(new Post(
                                                document.getData().get("postDate").toString(),
                                                document.getData().get("postUserId").toString(),
                                                document.getData().get("postText").toString(),
                                                document.getId().toString() ));
                                    }




                                }
                                itemsAdapter = new PostAdapter(MainActivity.this, posts);


                                listView.setAdapter(itemsAdapter);

                            }
                        }
                    });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirestore
                .collection("users")
                .document(mUserId).update("state", "Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirestore
                .collection("users")
                .document(mUserId).update("state", "Online");
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout_button:
                mFirestore
                        .collection("users")
                        .document(mUserId)
                        .update("state", "Offline")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FirebaseAuth.getInstance().signOut();
                                    sendToLogin();
                                }
                            }
                        });
                break;
            case R.id.profile_button:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.members_button:
                Intent membersIntent = new Intent(MainActivity.this, MembersActivity.class);
                startActivity(membersIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
