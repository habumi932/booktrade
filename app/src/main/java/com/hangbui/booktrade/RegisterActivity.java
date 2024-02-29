package com.hangbui.booktrade;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hangbui.booktrade.databinding.ActivityRegisterBinding;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    private View.OnClickListener button_register_clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            EditText email = (EditText)findViewById(R.id.editText_email);
            EditText password = (EditText)findViewById(R.id.editText_password);

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
                                Intent theIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(theIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Failure", "createUserWithEmail:failure", task.getException());
                                if(task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException){
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
        mAuth = FirebaseAuth.getInstance();

        binding.buttonRegister.setOnClickListener(button_register_clickListener);
        try {
            loadUniversities();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The CSV file failed to load", Toast.LENGTH_LONG).show();
        }
    }

    private void loadUniversities(){
        // Read CSV file to get a list of all US universities
        List<String> allUniversities = new ArrayList<String>();
        try {
            InputStream inputStream = getAssets().open("us_universities.csv");
            Reader bReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVReader reader = new CSVReader(bReader);
            String[] nextLine;
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
                new ArrayAdapter<>(RegisterActivity.this,android.R.layout.simple_spinner_item, universities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uniSpinner.setAdapter(adapter);
    }
}