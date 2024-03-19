package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_EMAIL;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_UNIVERSITY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private List<User> searchUsersResults;
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
            getSearchUsersResult(email, university);
        }
    };
    private AdapterView.OnItemClickListener listview_users_itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User thisUser = searchUsersResults.get(position);
            Log.i("On item click: ", thisUser.getName());
            replaceFragment(ViewUserFragment.newInstance(thisUser));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchUsersResults = new ArrayList<>();
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
        if(users.size() >= 1) {
            CustomAdapterSearchUsers adapter = new CustomAdapterSearchUsers(getActivity(), users);
            ListView listviewUsers = getView().findViewById(R.id.listview_users);
            listviewUsers.setOnItemClickListener(listview_users_itemClickListener);
            listviewUsers.setAdapter(adapter);
            searchUsersResults = users;
        } else {
            Toast.makeText(getActivity(), "No result found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSearchUsersResult(
            String email,
            String university
    ) {
        Query query = FirebaseFirestore.getInstance().collection(USERS_TABLE);
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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}