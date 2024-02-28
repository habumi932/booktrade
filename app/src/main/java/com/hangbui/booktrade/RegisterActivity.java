package com.hangbui.booktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hangbui.booktrade.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    private View.OnClickListener button_register_clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            // TODO: Add input textviews for user registration
            mAuth.createUserWithEmailAndPassword("hangbm2002@gmail.com", "testpassword")
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegisterActivity.this, "New user created successfully.",
                                        Toast.LENGTH_LONG).show();
                                Log.d("Success", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                // updateUI(user);
                                Intent theIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(theIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Failure", "createUserWithEmail:failure", task.getException());
                                if(task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException){
                                    Toast.makeText(RegisterActivity.this, "The email address is already in use.",
                                            Toast.LENGTH_LONG).show();
                                }

                                // updateUI(null);
                            }
                        }
                    });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.buttonRegister.setOnClickListener(button_register_clickListener);
    }

}