package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_EMAIL;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_NAME;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_PHOTO_URL;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_UNIVERSITY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hangbui.booktrade.databinding.ActivityLogoutBinding;

import java.util.Map;

public class LogoutActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENT_USER = "com.hangbui.booktrade.EXTRA_CURRENT_USER";

    private ActivityLogoutBinding binding;

    private FirebaseFirestore db;
    private User currentUser;

    private View.OnClickListener button_login_clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            // If user is logged in then go to home page, if not go to login page
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                getCurrentUser(user.getUid());
            }
            else {
                Intent theIntent = new Intent(LogoutActivity.this, LoginActivity.class);
                startActivity(theIntent);
            }
        }
    };
    private View.OnClickListener button_register_clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            Intent theIntent = new Intent(LogoutActivity.this, RegisterActivity.class);
            startActivity(theIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        binding.buttonLogin.setOnClickListener(button_login_clickListener);
        binding.buttonRegister.setOnClickListener(button_register_clickListener);
    }


    protected void getCurrentUser(String uid) {
        try {
            DocumentReference docRef = db.collection(USERS_TABLE).document(uid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> userData  = documentSnapshot.getData();
                    String name = (String) userData.get(USERS_TABLE_COL_NAME);
                    String email = (String) userData.get(USERS_TABLE_COL_EMAIL);
                    String photoUrl = (String) userData.get(USERS_TABLE_COL_PHOTO_URL);
                    String university = (String) userData.get(USERS_TABLE_COL_UNIVERSITY);
                    currentUser = new User(uid, email, name, photoUrl, university);
                    Intent theIntent = new Intent(LogoutActivity.this, HomeActivity.class);
                    Log.i("LogoutActivity", currentUser.getName());
                    theIntent.putExtra(EXTRA_CURRENT_USER, currentUser);
                    startActivity(theIntent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DB", "Error retrieving user from user id: " + uid);
        }
    }
}