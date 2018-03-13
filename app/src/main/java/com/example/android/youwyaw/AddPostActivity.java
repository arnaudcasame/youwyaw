package com.example.android.youwyaw;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private final String TAG = "nono";

    private Button postButton;
    private ImageView postImageView;
    private EditText postTextView;
    private ProgressBar postProgress;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String regId;
    private Uri mainImageUri = null;
    private String desc = null;
    private Map actualUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Create a storage reference from our app
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        regId = mAuth.getCurrentUser().getUid();

        postProgress = findViewById(R.id.post_progress);
        postButton = findViewById(R.id.post);
        postImageView = findViewById(R.id.post_image);
        postTextView = findViewById(R.id.post_description);

        mFirestore
                .collection("users")
                .document(regId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            actualUser = task.getResult().getData();
                            Log.i(TAG, task.getResult().getData().toString());
                        }else{
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                postButton.setClickable(false);
                postButton.setEnabled(false);
                desc = postTextView.getText().toString();
                Boolean yes;
                if(mainImageUri != null && !TextUtils.isEmpty(desc)){
                    postProgress.setVisibility(View.VISIBLE);
                     yes = true;
                    // Updates the current user's personal data
                    postInfos(yes, regId, desc);

                }else if(!TextUtils.isEmpty(desc)){
                    postProgress.setVisibility(View.VISIBLE);
                    yes = false;
                    // Updates the current user's personal data
                    postInfos(yes, regId, desc);

                }else{
                    Toast.makeText(AddPostActivity.this, "You must choose a picture before you can save your account", Toast.LENGTH_LONG).show();
                }
            }
        });

        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(AddPostActivity.this);
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

    private void postInfos(final Boolean hasImage, String regId, String desc) {
        // Create a new post with all infos
        Map<String, Object> post = new HashMap<>();
        post.put("postUserId", regId);
        post.put("postText", desc);
        post.put("postDate", FieldValue.serverTimestamp());

        mFirestore.collection("posts")
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        if(hasImage){
                            storePostImage(documentReference.getId().toString());
                        }else{
                            postProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddPostActivity.this, "Post posted successfully", Toast.LENGTH_LONG).show();
                            sendToMain();
                        }
                        Log.i(TAG, "Posted successfully with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPostActivity.this, "A problem occured posting to your wall", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Error adding post document", e);
                    }
                });
    }

    private void sendToMain(){
        Intent mainIntent = new Intent(AddPostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void storePostImage(final String randomName){
//      Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference image_path = mStorageRef.child("posts/").child(randomName);

        image_path.putFile(mainImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        postProgress.setVisibility(View.INVISIBLE);
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mFirestore
                                .collection("posts")
                                .document(randomName)
                                .update("postImageUrl", downloadUrl.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            sendToMain();
                                            mainImageUri = null;
                                            Toast.makeText(AddPostActivity.this, "Post posted successfully", Toast.LENGTH_LONG).show();
                                        }else{
                                            String e = task.getException().getMessage();
                                            Toast.makeText(AddPostActivity.this, "Post failed: "+e, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        postProgress.setVisibility(View.INVISIBLE);
                        // Handle unsuccessful uploads
                        // ...
                        String errorMessage = exception.getMessage();
                        Toast.makeText(AddPostActivity.this, "Error sa a :"+errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // The Picasso image picker activity launcher method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                postImageView.setImageURI(mainImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
