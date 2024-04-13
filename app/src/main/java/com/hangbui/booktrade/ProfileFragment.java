package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_BOOKS;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.EXTRA_FRIEND_IDS;
import static com.hangbui.booktrade.Constants.PADDING;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private static final String ARG_CURRENT_USER = "currentUser";
    private User currentUser;
    private List<Book> books;

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
                            deleteCurrentUser();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    };
    private View.OnClickListener button_edit_profile_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceFragment(new EditProfileFragment());
        }
    };
    private View.OnClickListener button_friends_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceFragment(new FriendsFragment());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
        books = getActivity().getIntent().getParcelableArrayListExtra(EXTRA_BOOKS);
        mAuth = FirebaseAuth.getInstance();
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
        Button editProfileButton = view.findViewById(R.id.button_edit_profile);
        Button friendsButton = view.findViewById(R.id.button_friends);
        logoutButton.setOnClickListener(button_logout_clickListener);
        deleteAccountButton.setOnClickListener(button_delete_account_clickListener);
        editProfileButton.setOnClickListener(button_edit_profile_clickListener);
        friendsButton.setOnClickListener(button_friends_clickListener);

        TextView textViewName = view.findViewById(R.id.textView_name);
        TextView textViewUniversity = view.findViewById(R.id.textView_university);
        TextView textViewNumFriends = view.findViewById(R.id.textView_num_friends);
        TextView textViewNumBooks = view.findViewById(R.id.textView_num_books);
        ImageView imageViewPhoto = view.findViewById(R.id.imageView_photo);

//        if(currentUser.getPhotoUrl().equals("")) {
//            imageViewPhoto.setImageResource(R.drawable.default_profile_pic);
//        } else {
//            // TODO: Implement displaying user profile pic
//        }
        textViewName.setText(currentUser.getName());
        textViewUniversity.setText(currentUser.getUniversity());
        int numBooks = books.size();
        if(numBooks <= 1) {
            textViewNumBooks.setText(PADDING + numBooks + " Book");
        } else {
            textViewNumBooks.setText(PADDING + numBooks + " Books");
        }
        int numFriends = getNumFriends();
        if(numFriends <= 1) {
            textViewNumFriends.setText(PADDING + numFriends + " Friend");
        } else {
            textViewNumFriends.setText(PADDING + numFriends + " Friends");
        }

    }

    private void deleteCurrentUser(){
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

    private int getNumFriends() {
        ArrayList<String> friendIds =
                getActivity()
                .getIntent()
                .getStringArrayListExtra(EXTRA_FRIEND_IDS);
        if(friendIds == null) {
            return 0;
        }
        return friendIds.size();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
