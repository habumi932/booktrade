package com.hangbui.booktrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hangbui.booktrade.databinding.ActivityLogoutBinding;

public class LogoutActivity extends AppCompatActivity {

    private ActivityLogoutBinding binding;

    private View.OnClickListener button_login_clickListener = new View.OnClickListener() {

        // TODO: Create login activity and configure login button
        public void onClick(View v) {
            // Intent theIntent = new Intent(LogoutActivity.this, HomeActivity.class);
            // startActivity(theIntent);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLogin.setOnClickListener(button_login_clickListener);

    }


}