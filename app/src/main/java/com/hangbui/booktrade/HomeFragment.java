package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hangbui.booktrade.databinding.FragmentHomeBinding;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String ARG_CURRENT_USER = "currentUser";
    private User currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        TextView welcomeMessage = view.findViewById(R.id.textView_welcome);
        welcomeMessage.setText("Welcome, " + currentUser.getName());
    }

}