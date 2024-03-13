package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksFragment extends Fragment {

    private User currentUser;

    public BooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {

    }
}