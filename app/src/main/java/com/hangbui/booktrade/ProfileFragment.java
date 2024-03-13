package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
    private static final String ARG_CURRENT_USER = "currentUser";
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_USER, user);
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
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
        mAuth = FirebaseAuth.getInstance();
        if(currentUser == null) {
            Log.e("ProfileFragment", "Current user is null");
        }
        else {
            Log.d("ProfileFragment", currentUser.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_profile, container, false);
       return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        Button logoutButton = view.findViewById(R.id.button_logout);
        Button deleteAccountButton = view.findViewById(R.id.button_delete_account);
        logoutButton.setOnClickListener(button_logout_clickListener);
        deleteAccountButton.setOnClickListener(button_delete_account_clickListener);
        TextView textViewName = view.findViewById(R.id.textView_name);
        TextView textViewUniversity = view.findViewById(R.id.textView_university);
        TextView textViewNumFriends = view.findViewById(R.id.textView_num_friends);
        TextView textViewNumBooks = view.findViewById(R.id.textView_num_books);
        textViewName.setText(currentUser.getName());
        textViewUniversity.setText(currentUser.getUniversity());
        textViewNumFriends.setText("   0 Friends");
        textViewNumBooks.setText("   0 Books");
    }
}
