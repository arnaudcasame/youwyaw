package com.example.android.youwyaw;

import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by Arnaud Casamé on 2/25/2018.
 */

public class User {
    public String firstname;
    public String lastname;
    public String avatar;
    public String regDate;
    public long date;
    public String regUser;
    public String surname;
    public String sexe;
    public String email;
    public String status;
    public String state;

    User(){}

    public User(DocumentSnapshot user) {
        this.regUser = user.getData().get("regUser").toString();
        this.avatar = user.getData().get("avatar").toString();
        this.regDate = user.getData().get("regDate").toString();
        this.surname = user.getData().get("surname").toString();
        this.email = user.getData().get("email").toString();
        if(user.getData().get("state") != null){
            this.state = user.getData().get("state").toString();
        }
    }

    public User(String firstname, String lastname, String avatar, long date, String regUser, String email, String sexe) {
        this.regUser = regUser;
        this.avatar = avatar;
        this.date = date;
        this.surname = "";
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.sexe = sexe;
    }

}
