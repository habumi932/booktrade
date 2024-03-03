package com.hangbui.booktrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hangbui.booktrade.databinding.ActivityLogoutBinding;

public class LogoutActivity extends AppCompatActivity {

    private ActivityLogoutBinding binding;

    private View.OnClickListener button_login_clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            // If user is logged in then go to home page, if not go to login page
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                Intent theIntent = new Intent(LogoutActivity.this, HomeActivity.class);
                startActivity(theIntent);
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

        binding.buttonLogin.setOnClickListener(button_login_clickListener);
        binding.buttonRegister.setOnClickListener(button_register_clickListener);
    }


}