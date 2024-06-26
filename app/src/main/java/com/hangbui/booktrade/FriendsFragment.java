package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_FRIEND_IDS;
import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<String> friendIds;

    private static final String ARG_NEW_FRIEND_ID = "newFriendId";
    // TODO: Rename and change types of parameters
    private String friendId;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String friendId) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NEW_FRIEND_ID, friendId);
        fragment.setArguments(args);
        return fragment;
    }

    // LISTENERS
    private View.OnClickListener button_friend_requests_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceFragment(new FriendRequestsFragment());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        friendIds = getActivity().getIntent().getStringArrayListExtra(EXTRA_FRIEND_IDS);
        if (getArguments() != null) {
            friendId = getArguments().getString(ARG_NEW_FRIEND_ID);
            friendIds.add(friendId);
            getActivity().getIntent().putStringArrayListExtra(EXTRA_FRIEND_IDS, friendIds);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button buttonFriendRequests = view.findViewById(R.id.button_friend_requests);
        buttonFriendRequests.setOnClickListener(button_friend_requests_clickListener);

        getFriendsList();
    }

    private void getFriendsList() {
        if(friendIds.isEmpty()) {
            updateFriendsList(new ArrayList<User>());
            return;
        }
        CollectionReference usersRef = db.collection(USERS_TABLE);
        Query query = usersRef.whereIn(USERS_TABLE_COL_ID, friendIds);
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
                            updateFriendsList(results);
                        } else {
                            Log.d("Book", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateFriendsList(List<User> users) {
        ListView listviewFriends = getView().findViewById(R.id.listview_friends);
        if (users.size() >= 1) {
            CustomAdapterSearchUsers adapter = new CustomAdapterSearchUsers(getActivity(), users);
            listviewFriends.setAdapter(adapter);
        } else {
            listviewFriends.setAdapter(null);
            // Toast.makeText(getActivity(), "No friend found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}