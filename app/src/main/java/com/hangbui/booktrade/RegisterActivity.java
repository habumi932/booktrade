package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_EMAIL;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_ID;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_NAME;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_PHOTO_URL;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_UNIVERSITY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hangbui.booktrade.databinding.ActivityRegisterBinding;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private View.OnClickListener button_register_clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            EditText email = findViewById(R.id.editText_email);
            EditText password = findViewById(R.id.editText_password);
            EditText name = findViewById(R.id.editText_name);
            Spinner uniSpinner = findViewById(R.id.spinner_university);
            String university = uniSpinner.getSelectedItem().toString();

            // Create user in Firebase Auth with email and password
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Registration success, save user data & go to home page
                                Toast.makeText(RegisterActivity.this, "New user created successfully.",
                                        Toast.LENGTH_LONG).show();
                                Log.d("Success", "createUserWithEmail:success");
                                String uid = mAuth.getCurrentUser().getUid();
                                User currentUser = createUser(uid, email.getText().toString(), name.getText().toString(), "", university);
                                sendUserToHomeActivity(currentUser);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Failure", "createUserWithEmail:failure", task.getException());
                                if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, "The email address is already in use.",
                                            Toast.LENGTH_LONG).show();
                                }
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

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.buttonRegister.setOnClickListener(button_register_clickListener);
        loadUniversities();
    }

    private void loadUniversities() {
        // Read CSV file to get a list of all US universities
        List<String> allUniversities = new ArrayList<String>();
        allUniversities.add(0, "Select University");
        try {
            InputStream inputStream = getAssets().open("us_universities.csv");
            Reader bReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVReader reader = new CSVReader(bReader);
            String[] nextLine;
            int id = 0;
            while ((nextLine = reader.readNext()) != null) {
                String university = nextLine[0];
                allUniversities.add(university);
            }
            // Remove first element which is the column name
            allUniversities.remove(0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }

        // Populate spinner with the university names
        String[] universities = new String[allUniversities.size()];
        universities = allUniversities.toArray(universities);
        Spinner uniSpinner = findViewById(R.id.spinner_university);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item, universities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uniSpinner.setAdapter(adapter);
    }

    private User createUser(String id, String email, String name, String photoUrl, String universityName) {
        Map<String, Object> user = new HashMap<>();
        user.put(USERS_TABLE_COL_ID, id);
        user.put(USERS_TABLE_COL_EMAIL, email);
        user.put(USERS_TABLE_COL_NAME, name);
        user.put(USERS_TABLE_COL_PHOTO_URL, photoUrl);
        user.put(USERS_TABLE_COL_UNIVERSITY, universityName);

        db.collection(USERS_TABLE).document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User", "New user successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("User", "Error writing new user document", e);
                    }
                });

        return new User(id, email, name, photoUrl, universityName);
    }

    private void sendUserToHomeActivity(User user) {
        Intent theIntent = new Intent(RegisterActivity.this, HomeActivity.class);
        theIntent.putExtra(EXTRA_CURRENT_USER, user);
        startActivity(theIntent);
    }
}