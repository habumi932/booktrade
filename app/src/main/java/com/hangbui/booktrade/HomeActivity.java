package com.hangbui.booktrade;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hangbui.booktrade.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}