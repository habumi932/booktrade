package com.hangbui.booktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hangbui.booktrade.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    private View.OnClickListener button_login_clickListener = new View.OnClickListener() {

        EditText email = binding.editTextEmail;
        EditText password = binding.editTextPassword;
        public void onClick(View v) {
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, go to home page
                                Log.d("Login", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent theIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(theIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Login", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            Intent theIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(theIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLogin.setOnClickListener(button_login_clickListener);
        mAuth = FirebaseAuth.getInstance();
    }
}