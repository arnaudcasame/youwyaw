package com.example.android.youwyaw;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Arnaud Casamé on 2/22/2018.
 */

public class Comment {
    public String avatar;
    public String commentDate;
    public String regUser;
    public String commentText;
    public String surname;
    public String commentId;

    public Comment(String commentDate, String regUser, String commentText, String commentId) {
        getPosterInfos(regUser);
        this.regUser = regUser;
        this.commentDate = commentDate;
        this.commentText = commentText;
        this.commentId = commentId;
    }

    private void getPosterInfos(String id){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            surname = task.getResult().getData().get("surname").toString();
                            avatar = task.getResult().getData().get("avatar").toString();
                        }else
                            Log.i("request user", "Get user doesn't work");
                    }
                });
    }
}
