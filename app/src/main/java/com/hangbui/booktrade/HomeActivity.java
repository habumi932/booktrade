package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_OWNER_ID;
import static com.hangbui.booktrade.Constants.EXTRA_BOOKS;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.EXTRA_FRIEND_IDS;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_RECEIVER_ID;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_SENDER_ID;
import static com.hangbui.booktrade.Constants.FRIENDSHIPS_TABLE_COL_STATUS;
import static com.hangbui.booktrade.Constants.FRIENDSHIP_STATUS_ACCEPTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hangbui.booktrade.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        currentUser = getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
        String uid = currentUser.getId();
        getBooks(uid);
        getFriends(uid);

        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if(itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if(itemId == R.id.books) {
                replaceFragment(new BooksFragment());
            } else if(itemId == R.id.search) {
                replaceFragment(new SearchFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void getBooks(String uid) {
        FirebaseFirestore.getInstance().collection(BOOKS_TABLE)
                .whereEqualTo(BOOKS_TABLE_COL_OWNER_ID, uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Book> books = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Book", document.getId() + " => " + document.getData());
                                Book book = document.toObject(Book.class);
                                books.add(book);
                            }
                            getIntent().putParcelableArrayListExtra(EXTRA_BOOKS, books);
                        } else {
                            Log.d("Book", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getFriends(String uid) {
        FirebaseFirestore.getInstance().collection(FRIENDSHIPS_TABLE)
                .where(Filter.and(
                    Filter.equalTo(FRIENDSHIPS_TABLE_COL_STATUS, FRIENDSHIP_STATUS_ACCEPTED),
                        (Filter.or(
                                Filter.equalTo(FRIENDSHIPS_TABLE_COL_RECEIVER_ID, uid),
                                Filter.equalTo(FRIENDSHIPS_TABLE_COL_SENDER_ID, uid))
                        )
                ))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> friendIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                String senderId = (String) data.get(FRIENDSHIPS_TABLE_COL_SENDER_ID);
                                String receiverId = (String) data.get(FRIENDSHIPS_TABLE_COL_RECEIVER_ID);
                                if(!senderId.equals(uid)) {
                                    friendIds.add(senderId);
                                } else {
                                    friendIds.add(receiverId);
                                }
                                getIntent().putStringArrayListExtra(EXTRA_FRIEND_IDS, friendIds);
                            }

                        } else {
                            Log.d("Friends", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
