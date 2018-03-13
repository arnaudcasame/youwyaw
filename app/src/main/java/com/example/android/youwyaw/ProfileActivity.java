package com.example.android.youwyaw;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final String TAG = "nono";
    private ImageView userAvatar = null;
    private Uri mainImageUri = null;
    private String surname = null;
    private Button saveUserSettingsButton = null;
    private EditText userSurnameView = null;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth = null;
    private FirebaseFirestore mFirestore = null;
    private ProgressBar imageProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        final String RegId = mAuth.getCurrentUser().getUid();


        imageProgress = findViewById(R.id.image_progress);
        userAvatar = findViewById(R.id.user_profile_image);
        userSurnameView = findViewById(R.id.user_profile_surname);
        saveUserSettingsButton = findViewById(R.id.save_user_profile_button);


        mFirestore
                .collection("users")
                .document(RegId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            if(task.getResult().getData().get("avatar") != null){

                                String avatarAddress = task.getResult().getData().get("avatar").toString();
                                // Displays the profile image with the picasso
                                Picasso.with(ProfileActivity.this)
                                        .load(avatarAddress)
                                        .placeholder(R.drawable.profile_placeholder)
                                        .error(R.drawable.profile_placeholder)
                                        .into(userAvatar, new com.squareup.picasso.Callback(){
                                            @Override
                                            public void onSuccess() {
//                                                Toast.makeText(ProfileActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onError() {
                                                Toast.makeText(ProfileActivity.this, "Profile picture not uploaded", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                // Displays the surname with setText method
                                if( task.getResult().getData().get("surname") != null){
                                    userSurnameView.setText(task.getResult().getData().get("surname").toString());
                                }

                            }
                            Log.i(TAG, task.getResult().getId() + " => " + task.getResult().getData().get("avatar"));
                        }else{
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



        saveUserSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surname = userSurnameView.getText().toString();
                if(mainImageUri != null){
                    imageProgress.setVisibility(View.VISIBLE);
//                    Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
                    StorageReference image_path = mStorageRef.child("images/").child(RegId);

                    image_path.putFile(mainImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageProgress.setVisibility(View.INVISIBLE);
                                    // Get a URL to the uploaded content
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    // Updates the current user's personal data
                                    updateProfile(downloadUrl, RegId, surname);
                                    mainImageUri = null;
                                    Toast.makeText(ProfileActivity.this, "Profile picture displayed successfully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    imageProgress.setVisibility(View.INVISIBLE);
                                    // Handle unsuccessful uploads
                                    // ...
                                    String errorMessage = exception.getMessage();
                                    Toast.makeText(ProfileActivity.this, "Error sa a :"+errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                }else if(!TextUtils.isEmpty(surname)){
                    updateProfile(null, RegId, surname);
                }else{
                    Toast.makeText(ProfileActivity.this, "You must choose a picture before you can save your account", Toast.LENGTH_LONG).show();
                }
            }
        });

        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(
                                ProfileActivity.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }else{
                        BringImagePicker();
                    }
                }else{
                        BringImagePicker();
                }
            }
        });
    }// End of the onCreate method


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }else {

        }
    }

    private void updateProfile(Uri url, final String regId, final String lilSurname){
        if(url != null) {
            mFirestore.collection("users")
                    .document(regId)
                    .update("avatar", url.toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFirestore.collection("users")
                                    .document(regId)
                                    .update("surname", lilSurname)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.w(TAG, "Success updating document");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });

                            Log.w(TAG, "Success updating document");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });
        }else{
            mFirestore.collection("users")
                    .document(regId)
                    .update("surname", lilSurname)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.w(TAG, "Success updating document");
                            Toast.makeText(ProfileActivity.this, "Name updated successfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });
        }
    }

    // This method triggers the activity launcher method
    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ProfileActivity.this);

    }

    // The Picasso image picker activity launcher method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                userAvatar.setImageURI(mainImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
