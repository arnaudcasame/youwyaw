package com.example.android.youwyaw;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Arnaud Casamé on 2/22/2018.
 */

public class Post {
    public String firstname;
    public String lastname;
    public String avatar;
    public String postDate;
    public String regUser;
    public String postText;
    public String surname;
    public String postImageUrl;
    public String postId;

    public Post(String postDate, String regUser, String postText, String postImageUrl, String postId) {
        getPosterInfos(regUser);
        this.regUser = regUser;
        this.postDate = postDate;
        this.postText = postText;
        this.postImageUrl = postImageUrl;
        this.postId = postId;
    }

    public Post(String postDate, String regUser, String postText, String postId) {
        getPosterInfos(regUser);
        this.regUser = regUser;
        this.postDate = postDate;
        this.postText = postText;
        this.postId = postId;
    }

    public Boolean hasImage(){
        if(TextUtils.isEmpty(postImageUrl)){
            return false;
        }else{
            return true;
        }
    }

    private void getPosterInfos(String id){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    surname = task.getResult().getData().get("surname").toString();
                    firstname = task.getResult().getData().get("firstname").toString();
                    lastname = task.getResult().getData().get("lastname").toString();
                    avatar = task.getResult().getData().get("avatar").toString();
                }
            }
        });
    }
}
