package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.EXTRA_FRIEND_REQUESTS_IDS;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_RECEIVER_ID;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_SENDER_ID;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_STATUS;
import static com.hangbui.booktrade.Constants.FRIENDSHIP_STATUS_ACCEPTED;
import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_ID;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendRequestsFragment extends Fragment {

    private User currentUser;
    private List<User> friendRequestUsers;
    private FirebaseFirestore db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendRequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendRequestsFragment newInstance(String param1, String param2) {
        FriendRequestsFragment fragment = new FriendRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // LISTENERS
    private AdapterView.OnItemClickListener listview_friend_requests_itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
            User sender = friendRequestUsers.get(position);
            String senderId = sender.getId();
            String receiverId = currentUser.getId();
            String senderName = sender.getName();
            String senderUniversity = sender.getUniversity();
            myBuilder
                    .setTitle("Friend Request")
                    .setMessage("You received a friend request from " + senderName + " - "
                            + senderUniversity + ".")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            acceptFriendRequest(senderId, receiverId);
                        }
                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // TODO: Delete friend request
                        }
                    });
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
        friendRequestUsers = new ArrayList<>();
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_requests, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getFriendRequestsList();
    }

    private void updateFriendRequestsList(List<User> users) {
        ListView listviewFriendRequests = getView().findViewById(R.id.listview_friend_requests);
        if (users.size() >= 1) {
            CustomAdapterSearchUsers adapter = new CustomAdapterSearchUsers(getActivity(), users);
            listviewFriendRequests.setOnItemClickListener(listview_friend_requests_itemClickListener);
            listviewFriendRequests.setAdapter(adapter);
            friendRequestUsers = users;
        } else {
            listviewFriendRequests.setAdapter(null);
            Toast.makeText(getActivity(), "No friend request found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getFriendRequestsList() {
        ArrayList<String> friendRequestIds = getActivity()
                .getIntent()
                .getStringArrayListExtra(EXTRA_FRIEND_REQUESTS_IDS);
        CollectionReference usersRef = db.collection(USERS_TABLE);
        Query query = usersRef.whereIn(USERS_TABLE_COL_ID, friendRequestIds);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> results = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                Log.d("Friend request user: ", user.getId());
                                results.add(user);
                            }
                            updateFriendRequestsList(results);
                        } else {
                            Log.d("Book", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void acceptFriendRequest(String senderId, String receiverId) {
        CollectionReference friendshipRef = db.collection(FRIENDSHIPS_TABLE);
        Query query = friendshipRef
                .whereEqualTo(FRIENDSHIPS_TABLE_COL_SENDER_ID, senderId)
                .whereEqualTo(FRIENDSHIPS_TABLE_COL_RECEIVER_ID, receiverId);
        query.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String documentId = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentId = (String) document.getId();
                            }
                            updateFriendRequestStatusToAccepted(documentId);
                        } else {
                            Log.e("Friendship", "Error getting friendship documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateFriendRequestStatusToAccepted(String documentId) {
        DocumentReference friendshipRef = db.collection(FRIENDSHIPS_TABLE).document(documentId);
        friendshipRef
                .update(FRIENDSHIPS_TABLE_COL_STATUS, FRIENDSHIP_STATUS_ACCEPTED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // TODO: Add friend to friends list
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Friendship update FAILED", "Failed to set friendship to ACCEPTED - " + documentId);
                    }
                });
    }
}
