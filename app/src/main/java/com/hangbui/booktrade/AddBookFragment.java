package com.hangbui.booktrade;

import static android.content.Intent.getIntent;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_AUTHORS;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_DESCRIPTION;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_GENRE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_NAME;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_OWNER_ID;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBookFragment extends Fragment {

    private User currentUser;

    public AddBookFragment() {
        // Required empty public constructor
    }

    // LISTENERS
    private View.OnClickListener button_add_book_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View theView = getView();
            EditText edittextBookName = theView.findViewById(R.id.editText_book_name);
            EditText edittextAuthors = theView.findViewById(R.id.editText_book_authors);
            Spinner spinnerGenre = theView.findViewById(R.id.spinner_genre);
            EditText edittextDescription = theView.findViewById(R.id.editText_book_description);
            String ownerId = currentUser.getId();
            String name = edittextBookName.getText().toString();
            String authors = edittextAuthors.getText().toString();
            String genre = spinnerGenre.getSelectedItem().toString();
            String description = edittextDescription.getText().toString();
            createBook(ownerId, name, authors, genre, description);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        Spinner spinnerGenre = view.findViewById(R.id.spinner_genre);
        // TODO: Create genre list for spinner
        String[] genres = {"Fiction novel", "Thriller", "Romance"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, genres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);
        Button buttonAddBook = view.findViewById(R.id.button_add);
        buttonAddBook.setOnClickListener(button_add_book_onClickListener);
    }

    private void createBook(
            String ownerId,
            String name,
            String authors,
            String genre,
            String description
    ) {
        Map<String, Object> book = new HashMap<>();
        book.put(BOOKS_TABLE_COL_OWNER_ID, ownerId);
        book.put(BOOKS_TABLE_COL_NAME, name);
        book.put(BOOKS_TABLE_COL_AUTHORS, authors);
        book.put(BOOKS_TABLE_COL_GENRE, genre);
        book.put(BOOKS_TABLE_COL_DESCRIPTION, description);

        FirebaseFirestore.getInstance().collection(BOOKS_TABLE).document()
                .set(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "New book added successfully.",
                                Toast.LENGTH_LONG).show();
                        Log.d("Book", "New book successfully written!");
                        Book newBook = new Book("", ownerId, name, authors, genre, description);
                        replaceFragment(BooksFragment.newInstance(newBook));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Book", "Error writing new book document", e);
                    }
                });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}