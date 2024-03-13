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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
                                // Sign in success, go to home page
                                Toast.makeText(RegisterActivity.this, "New user created successfully.",
                                        Toast.LENGTH_LONG).show();
                                Log.d("Success", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                createUser(user.getUid(), email.getText().toString(), name.getText().toString(), "", university);
                                getCurrentUser(user.getUid());
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

    private void createUser(String id, String email, String name, String photoUrl, String universityName) {
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
                    User currentUser = new User(uid, email, name, photoUrl, university);
                    Intent theIntent = new Intent(RegisterActivity.this, HomeActivity.class);
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