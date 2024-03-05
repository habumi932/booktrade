package com.hangbui.booktrade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hangbui.booktrade.databinding.FragmentProfileBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // LISTENERS
    private View.OnClickListener button_logout_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                mAuth.signOut();
                Toast.makeText(getActivity(), "User logged out successfully.",
                        Toast.LENGTH_LONG).show();
                Intent theIntent = new Intent(getActivity(), LogoutActivity.class);
                startActivity(theIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "An error has occurred.", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener button_delete_account_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Account deleted successfully.",
                                                            Toast.LENGTH_LONG).show();
                                                    Intent theIntent = new Intent(getActivity(), LogoutActivity.class);
                                                    startActivity(theIntent);
                                                }
                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "An error has occurred.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_profile, container, false);
       Button logoutButton = view.findViewById(R.id.button_logout);
       Button deleteAccountButton = view.findViewById(R.id.button_delete_account);
       logoutButton.setOnClickListener(button_logout_clickListener);
       deleteAccountButton.setOnClickListener(button_delete_account_clickListener);
       return view;
    }
}