package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_OWNER_ID;
import static com.hangbui.booktrade.Constants.EXTRA_BOOKS;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hangbui.booktrade.databinding.ActivityHomeBinding;

import java.util.ArrayList;

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
        getBooks(currentUser.getId());

        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if(itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if(itemId == R.id.books) {
                replaceFragment(new BooksFragment());
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
}
