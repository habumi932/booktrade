package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.hangbui.booktrade.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        User currentUser = intent.getParcelableExtra(EXTRA_CURRENT_USER);
        replaceFragment(new HomeFragment());

        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if(itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if(itemId == R.id.books) {
                replaceFragment(new BooksFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
