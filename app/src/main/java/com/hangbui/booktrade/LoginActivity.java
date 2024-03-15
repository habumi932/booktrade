package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.USERS_TABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hangbui.booktrade.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private View.OnClickListener button_login_clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            // Sign in user in Firebase Auth with email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();
                                // Sign in success, go to home page
                                Log.d("Sign in", "signInWithEmail:success");
                                try {
                                    DocumentReference docRef = db.collection(USERS_TABLE).document(uid);
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User currentUser = documentSnapshot.toObject(User.class);
                                            sendUserToHomeActivity(currentUser);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("DB", "Error retrieving user from user id: " + uid);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Sign in", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding.buttonLogin.setOnClickListener(button_login_clickListener);
    }

    private void sendUserToHomeActivity(User user) {
        Intent theIntent = new Intent(LoginActivity.this, HomeActivity.class);
        theIntent.putExtra(EXTRA_CURRENT_USER, user);
        startActivity(theIntent);
    }
}