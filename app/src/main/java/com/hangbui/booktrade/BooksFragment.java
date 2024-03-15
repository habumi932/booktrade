package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_BOOKS;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksFragment extends Fragment {

    private User currentUser;
    private List<Book> books;

    public BooksFragment() {
        // Required empty public constructor
    }

    // lISTENERS
    private AdapterView.OnItemClickListener listview_books_itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
            Book thisBook = books.get(position);
            String description = thisBook.getDescription();
            myBuilder   .setTitle("Book Description")
                    .setMessage(description);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
        books = getActivity().getIntent().getParcelableArrayListExtra(EXTRA_BOOKS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        updateListviewBooks(view);
    }

    private void updateListviewBooks(View view) {
        CustomAdapter adapter = new CustomAdapter(getActivity(), books);
        ListView listviewBooks = view.findViewById(R.id.listview_books);
        listviewBooks.setOnItemClickListener(listview_books_itemClickListener);
        listviewBooks.setAdapter(adapter);
    }
}