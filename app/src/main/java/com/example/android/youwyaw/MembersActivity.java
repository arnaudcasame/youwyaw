package com.example.android.youwyaw;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MembersActivity extends AppCompatActivity {

    private FirebaseFirestore mStore;
    private ListView usersList;
    private UserAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        usersList = findViewById(R.id.members_list);



        mStore = FirebaseFirestore.getInstance();
        mStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<User> users = new ArrayList<User>();
                for(DocumentSnapshot user: documentSnapshots){
                    users.add(new User(user));
                }
                itemsAdapter = new UserAdapter(MembersActivity.this, users);
                usersList.setAdapter(itemsAdapter);
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
}
