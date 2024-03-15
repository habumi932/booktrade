package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.USERS_TABLE;

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

public class LogoutActivity extends AppCompatActivity {

    private ActivityLogoutBinding binding;

    private FirebaseFirestore db;

    private View.OnClickListener button_login_clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            // If user is logged in then go to home page, if not go to login page
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                String uid = user.getUid();
                try {
                    DocumentReference docRef = db.collection(USERS_TABLE).document(uid);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User currentUser = documentSnapshot.toObject(User.class);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("DB", "Error retrieving user from user id: " + uid);
                }
            }
            else {
                sendUserToLoginActivity();
            }
        }
    };
    private View.OnClickListener button_register_clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            sendUserToRegisterActivity();
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

    private void sendUserToRegisterActivity(){
        Intent theIntent = new Intent(LogoutActivity.this, RegisterActivity.class);
        startActivity(theIntent);
    }
    private void sendUserToLoginActivity(){
        Intent theIntent = new Intent(LogoutActivity.this, LoginActivity.class);
        startActivity(theIntent);
    }
    private void sendUserToHomeActivity(User user) {
        Intent theIntent = new Intent(LogoutActivity.this, HomeActivity.class);
        theIntent.putExtra(EXTRA_CURRENT_USER, user);
        startActivity(theIntent);
    }
}