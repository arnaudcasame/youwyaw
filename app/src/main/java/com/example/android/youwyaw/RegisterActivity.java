package com.example.android.youwyaw;


import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;




import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private FirebaseAuth mAuth = null;
    private DatabaseReference mDatabase;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore mFirestore;


    // UI references.
    private EditText mFirstnameView;
    private EditText mLastnameView;
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private ProgressBar mProgressView;
    private Button mRegistrationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        mFirstnameView = (EditText) findViewById(R.id.registration_firstname);
        mLastnameView = (EditText) findViewById(R.id.registration_lastname);
        mMaleRadio = (RadioButton) findViewById(R.id.registration_user_male);
        mFemaleRadio = (RadioButton) findViewById(R.id.registration_user_female);
        mEmailView = (EditText) findViewById(R.id.registration_email);
        mPasswordView = (EditText) findViewById(R.id.registration_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.registration_confirmation_password);
        mProgressView = (ProgressBar) findViewById(R.id.registration_progress);
        mRegistrationButton = (Button) findViewById(R.id.registration_button);


        mRegistrationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String firstname = mFirstnameView.getText().toString();
                final String lastname = mLastnameView.getText().toString();
                final String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                String passwordConfirmation = mConfirmPasswordView.getText().toString();
                final String gender = mMaleRadio.isChecked() ? "gason" : "fi";
                if( !TextUtils.isEmpty(firstname) &&
                    !TextUtils.isEmpty(lastname) &&
                    !TextUtils.isEmpty(email) &&
                    !TextUtils.isEmpty(password) &&
                    !TextUtils.isEmpty(passwordConfirmation) &&
                    !TextUtils.isEmpty(gender)){

                    if(password.equals(passwordConfirmation)){
                        mProgressView.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String uId = mAuth.getCurrentUser().getUid();
                                    String avatarFemaleUrl = "https://firebasestorage.googleapis.com/v0/b/youwyaw.appspot.com/o/images%2Fgirl-509.png";
                                    String avatarMaleUrl = "https://firebasestorage.googleapis.com/v0/b/youwyaw.appspot.com/o/images%2Fboy-509.png";
                                    String avatar = mMaleRadio.isChecked() ? avatarMaleUrl : avatarFemaleUrl;
                                    writeNewUser(uId, firstname, lastname, email, gender);
                                    sendToMain();
                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error :"+error, Toast.LENGTH_LONG).show();
                                }
                                mProgressView.setVisibility(View.INVISIBLE);
                            }
                        });
                    }else{
                        Toast.makeText(RegisterActivity.this, "Password doesn't match with the confirmation", Toast.LENGTH_LONG).show();
                    }
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

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void writeNewUser(final String userId, String firstname, String lastname, String email, String gender) {

        // Create a new user with a first and last name
        Map<String, Object> userFirestore = new HashMap<>();
        userFirestore.put("firstname", firstname);
        userFirestore.put("lastname", lastname);
        userFirestore.put("email", email);
        userFirestore.put("sexe", gender);
        userFirestore.put("regUser", userId);
        userFirestore.put("regDate", FieldValue.serverTimestamp());

        // Add a new document with a generated ID
        mFirestore.collection("users").document(userId).set(userFirestore)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "DocumentSnapshot added with ID: " + userId, Toast.LENGTH_LONG).show();
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Error adding document: "+error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}

