package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.EXTRA_BOOKS;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

    private static final String ARG_NEW_BOOK = "newBook";
    public BooksFragment() {
        // Required empty public constructor
    }

    public static BooksFragment newInstance(Book book) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NEW_BOOK, book);
        fragment.setArguments(args);
        return fragment;
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
    private View.OnClickListener button_add_book_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceFragment(new AddBookFragment());
        }
    };
    private View.OnClickListener button_trade_requests_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            replaceFragment(new TradeRequestsFragment());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
        books = getActivity().getIntent().getParcelableArrayListExtra(EXTRA_BOOKS);
        if(getArguments() != null) {
            Book newBook = getArguments().getParcelable(ARG_NEW_BOOK);
            books.add(newBook);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        Button buttonAddBook = view.findViewById(R.id.button_add_book);
        Button tradeRequestsButton = view.findViewById(R.id.button_trade_requests);
        buttonAddBook.setOnClickListener(button_add_book_onClickListener);
        tradeRequestsButton.setOnClickListener(button_trade_requests_onClickListener);
        updateListviewBooks(view);
    }

    private void updateListviewBooks(View view) {
        CustomAdapterBooks adapter = new CustomAdapterBooks(getActivity(), books);
        ListView listviewBooks = view.findViewById(R.id.listview_books);
        listviewBooks.setOnItemClickListener(listview_books_itemClickListener);
        listviewBooks.setAdapter(adapter);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}