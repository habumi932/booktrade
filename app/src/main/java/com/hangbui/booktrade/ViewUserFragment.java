package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_OWNER_ID;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_RECEIVER_ID;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_SENDER_ID;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_STATUS;
import static com.hangbui.booktrade.Constants.FRIENDSHIP_STATUS_ACCEPTED;
import static com.hangbui.booktrade.Constants.FRIENDSHIP_STATUS_RECEIVED;
import static com.hangbui.booktrade.Constants.FRIENDSHIP_STATUS_REQUESTED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewUserFragment extends Fragment {

    private static final String ARG_USER = "user";

    private User theUser;
    private User currentUser;
    private List<Book> theUsersBooks;

    public ViewUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewUserFragment newInstance(User user) {
        ViewUserFragment fragment = new ViewUserFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            theUser = getArguments().getParcelable(ARG_USER);
        }
        theUsersBooks = new ArrayList<>();
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
    }

    // lISTENERS
    private AdapterView.OnItemClickListener listview_users_books_itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
            Book thisBook = theUsersBooks.get(position);
            String description = thisBook.getDescription();
            myBuilder   .setTitle("Book Description")
                    .setMessage(description);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
        }
    };
    private View.OnClickListener button_add_friend_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("ViewUserFragment", "Button clicked");
            String senderId = currentUser.getId();
            String receiverId = theUser.getId();
            sendFriendRequest(senderId, receiverId);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView textViewName = view.findViewById(R.id.textView_name);
        TextView textViewUniversity = view.findViewById(R.id.textView_university);
        TextView textViewNumFriends = view.findViewById(R.id.textView_num_friends);
        TextView textViewNumBooks = view.findViewById(R.id.textView_num_books);
        ImageView imageViewPhoto = view.findViewById(R.id.imageView_photo);
        Button addFriendButton = view.findViewById(R.id.button_add_friend_2);

        if(theUser.getPhotoUrl().equals("")) {
            imageViewPhoto.setImageResource(R.drawable.default_profile_pic);
        } else {
            // TODO: Implement displaying user profile pic
        }
        textViewName.setText(theUser.getName());
        textViewUniversity.setText(theUser.getUniversity());
        getTheUsersBooks(theUser.getId());
        String currUserId = currentUser.getId();
        String theUserId = theUser.getId();
        updateFriendButton(currUserId, theUserId);
    }

    private void updateListViewUsersBooks(List<Book> books) {
        ListView listviewUsersBooks = getView().findViewById(R.id.listview_users_books);
        theUsersBooks = books;
        if(books.size() >= 1) {
            CustomAdapterBooks adapter = new CustomAdapterBooks(getActivity(), books);
            listviewUsersBooks.setOnItemClickListener(listview_users_books_itemClickListener);
            listviewUsersBooks.setAdapter(adapter);
        } else {
            listviewUsersBooks.setAdapter(null);
        }
    }
    private void getTheUsersBooks(String uid) {
        FirebaseFirestore.getInstance().collection(BOOKS_TABLE)
                .whereEqualTo(BOOKS_TABLE_COL_OWNER_ID, uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Book> books = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Book book = document.toObject(Book.class);
                                books.add(book);
                            }
                            updateListViewUsersBooks(books);
                        } else {
                            Log.d("Book", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public boolean sendFriendRequest(String senderId, String receiverId) {
        Map<String, Object> friendship = new HashMap<>();
        friendship.put(FRIENDSHIPS_TABLE_COL_SENDER_ID, senderId);
        friendship.put(FRIENDSHIPS_TABLE_COL_RECEIVER_ID, receiverId);
        friendship.put(FRIENDSHIPS_TABLE_COL_STATUS, FRIENDSHIP_STATUS_REQUESTED);

        Map<String, Object> friendship2 = new HashMap<>();
        friendship2.put(FRIENDSHIPS_TABLE_COL_RECEIVER_ID, senderId);
        friendship2.put(FRIENDSHIPS_TABLE_COL_SENDER_ID, receiverId);
        friendship2.put(FRIENDSHIPS_TABLE_COL_STATUS, FRIENDSHIP_STATUS_RECEIVED);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FRIENDSHIPS_TABLE).document()
                .set(friendship)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateAddFriendButtonHelper(FRIENDSHIP_STATUS_REQUESTED);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Friendship", "Error writing new friendship");
                    }
                });
        db.collection(FRIENDSHIPS_TABLE).document()
                .set(friendship2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
        return false;
    }

    private void updateFriendButton(String senderId, String receiverId) {
        FirebaseFirestore.getInstance().collection(FRIENDSHIPS_TABLE)
                .whereEqualTo(FRIENDSHIPS_TABLE_COL_SENDER_ID, senderId)
                .whereEqualTo(FRIENDSHIPS_TABLE_COL_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String status = "";
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            status = (String) document.get(FRIENDSHIPS_TABLE_COL_STATUS);
                        }
                        updateAddFriendButtonHelper(status);
                    }
                });
    }

    private void updateAddFriendButtonHelper(String status) {
        View view = getView();
        Button addFriendButton = view.findViewById(R.id.button_add_friend_2);
        if (status.equals("")) {
            addFriendButton.setText(R.string.button_add_friend);
            addFriendButton.setActivated(true);
            addFriendButton.setOnClickListener(button_add_friend_clickListener);
        }
        else if (status.equals(FRIENDSHIP_STATUS_REQUESTED)) {
            addFriendButton.setText(R.string.button_friend_request_requested);
            addFriendButton.setActivated(false);
            addFriendButton.setTextColor(getResources().getColor(R.color.yellow));
            addFriendButton.setOnClickListener(null);
        }
        else if (status.equals(FRIENDSHIP_STATUS_ACCEPTED)) {
            addFriendButton.setText(R.string.button_friend_added);
            addFriendButton.setActivated(false);
            addFriendButton.setTextColor(getResources().getColor(R.color.green));
            addFriendButton.setOnClickListener(null);
        }
    }
}
