package com.hangbui.booktrade;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchBooksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchBooksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchBooksFragment newInstance(String param1, String param2) {
        SearchBooksFragment fragment = new SearchBooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_books, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        loadUniversities();
        loadGenres();
    }

    private void loadGenres() {
        // Read CSV file to get a list of all book genres
        List<String> allGenres = new ArrayList<>();
        try {
            InputStream inputStream = getActivity().getAssets().open("book_genres.csv");
            Reader bReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVReader reader = new CSVReader(bReader);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String genre = nextLine[0];
                allGenres.add(genre);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        allGenres.add(0, "All");
        allGenres.add(0, "Select Genre");

        // Populate spinner with the genres
        String[] genres = new String[allGenres.size()];
        genres = allGenres.toArray(genres);
        Spinner genreSpinner = getView().findViewById(R.id.spinner_genres);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, genres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);
    }

    private void loadUniversities() {
        // Read CSV file to get a list of all US universities
        List<String> allUniversities = new ArrayList<String>();
        try {
            InputStream inputStream = getActivity().getAssets().open("us_universities.csv");
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
}