package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_EMAIL;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_UNIVERSITY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    // LISTENERS
    private View.OnClickListener button_search_users_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View thisView = getView();
            TextView textviewEmail = thisView.findViewById(R.id.edittext_search_user_email);
            Spinner spinnerUniversity = thisView.findViewById(R.id.spinner_universities);
            String email = textviewEmail.getText().toString();
            String university = spinnerUniversity.getSelectedItem().toString();
            Log.d("SearchFragment", "User name & uni: " + email + "  " + university);
            getSearchUsersResult(email, university);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button buttonSearchUsers = view.findViewById(R.id.button_search_users);
        buttonSearchUsers.setOnClickListener(button_search_users_onClickListener);
        loadUniversities();
    }

    private void loadUniversities() {
        // Read CSV file to get a list of all US universities
        List<String> allUniversities = new ArrayList<String>();
        try {
            InputStream inputStream = getActivity().getAssets().open("us_universities.csv");
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
            Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        allUniversities.add(0, "All");
        allUniversities.add(0, "Select University");

        // Populate spinner with the university names
        String[] universities = new String[allUniversities.size()];
        universities = allUniversities.toArray(universities);
        Spinner uniSpinner = getView().findViewById(R.id.spinner_universities);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, universities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uniSpinner.setAdapter(adapter);
    }

    private void updateSearchUsersResults(List<User> users) {
        CustomAdapterSearchUsers adapter = new CustomAdapterSearchUsers(getActivity(), users);
        ListView listviewUsers = getView().findViewById(R.id.listview_users);
        listviewUsers.setAdapter(adapter);
    }

    private void getSearchUsersResult(
            String email,
            String university
    ) {
        Query query = FirebaseFirestore.getInstance().collection(USERS_TABLE);
//                .whereEqualTo(USERS_TABLE_COL_EMAIL, email)
//                .whereEqualTo(USERS_TABLE_COL_UNIVERSITY, university);
        if (!email.equals("")) {
            query = query.whereEqualTo(USERS_TABLE_COL_EMAIL, email);
        }
        if (!university.equals("")
                && !university.equals("Select University")
                && !university.equals("All")) {
            query = query.whereEqualTo(USERS_TABLE_COL_UNIVERSITY, university);
        }
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> results = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Search Users", document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                results.add(user);
                            }
                            updateSearchUsersResults(results);
                        } else {
                            Log.d("Book", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}