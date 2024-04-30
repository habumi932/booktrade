package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_GENRE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_OWNER_ID;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_BOOK_ID;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_RECEIVER_ID;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_SENDER_ID;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_SENDER_NAME;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_SENDER_UNIVERSITY;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_STATUS;
import static com.hangbui.booktrade.Constants.BOOK_REQUEST_STATUS_REQUESTED;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.EXTRA_FRIEND_IDS;
import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_EMAIL;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_ID;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_NAME;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_UNIVERSITY;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchBooksFragment extends Fragment {

    private User currentUser;
    private List<Book> searchBooksResults;
    private FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchBooksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchBooksFragment newInstance(String param1, String param2) {
        SearchBooksFragment fragment = new SearchBooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // LISTENERS
    private View.OnClickListener button_search_books_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View theView) {
            View view = getView();
            Spinner uniSpinner = view.findViewById(R.id.spinner_universities);
            String university = uniSpinner.getSelectedItem().toString();
            Spinner genreSpinner = view.findViewById(R.id.spinner_genres);
            String genre = genreSpinner.getSelectedItem().toString();
            CheckBox isFriendsBookCheckbox = view.findViewById(R.id.checkBox_isFriendsBook);
            boolean isFriendsBook = isFriendsBookCheckbox.isChecked();
            getSearchBooksResults(genre, university, isFriendsBook);
        }
    };
    private AdapterView.OnItemClickListener listview_books_itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
            Book thisBook = searchBooksResults.get(position);
            String senderId = currentUser.getId();
            String receiverId = thisBook.getOwnerId();
            String bookId = thisBook.getBookId();
            String description = thisBook.getDescription();
            myBuilder
                    .setTitle("Book Description")
                    .setMessage(description)
                    .setPositiveButton("Request book",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sendTradeRequest(senderId, receiverId, bookId);
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
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
        searchBooksResults = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_books, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadUniversities();
        loadGenres();
        Button searchBooksButton = view.findViewById(R.id.button_search_books_2);
        searchBooksButton.setOnClickListener(button_search_books_clickListener);
    }

    private void loadGenres() {
        // Read CSV file to get a list of all book genres
        List<String> allGenres = new ArrayList<>();
        try {
            InputStream inputStream = getActivity().getAssets().open("book_genres.csv");
            Reader bReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVReader reader = new CSVReader(bReader);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String genre = nextLine[0];
                allGenres.add(genre);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        allGenres.add(0, "All");
        allGenres.add(0, "Select Genre");

        // Populate spinner with the genres
        String[] genres = new String[allGenres.size()];
        genres = allGenres.toArray(genres);
        Spinner genreSpinner = getView().findViewById(R.id.spinner_genres);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, genres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);
    }

    private void loadUniversities() {
        // Read CSV file to get a list of all US universities
        List<String> allUniversities = new ArrayList<String>();
        try {
            InputStream inputStream = getActivity().getAssets().open("us_universities.csv");
            Reader bReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVReader reader = new CSVReader(bReader);
            String[] nextLine;
            int id = 0;
            while ((nextLine = reader.readNext()) != null) {
                String university = nextLine[0];
                allUniversities.add(university);
            }
            // Remove first element which is the column name
            allUniversities.remove(0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        allUniversities.add(0, "All");
        allUniversities.add(0, "Select University");

        // Populate spinner with the university names
        String[] universities = new String[allUniversities.size()];
        universities = allUniversities.toArray(universities);
        Spinner uniSpinner = getView().findViewById(R.id.spinner_universities);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, universities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uniSpinner.setAdapter(adapter);
    }

    private void getSearchBooksResults(String genre, String university, boolean isFriendsBook) {
        ArrayList<String> friendIds = getActivity().getIntent().getStringArrayListExtra(EXTRA_FRIEND_IDS);
        Query query = db.collection(USERS_TABLE);
        if (!university.equals("")
                && !university.equals("Select University")
                && !university.equals("All")) {
            query = query.whereEqualTo(USERS_TABLE_COL_UNIVERSITY, university);
        }
        if (isFriendsBook && !friendIds.isEmpty()) {
            query = query.whereIn(USERS_TABLE_COL_ID, friendIds);
        }
        else if (isFriendsBook && friendIds.isEmpty()) {
            searchBooksResults.clear();
            updateListViewBooks(searchBooksResults);
            return;
        }
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                searchBooksResults.clear();
                                updateListViewBooks(searchBooksResults);
                                return;
                            }
                            ArrayList<String> userIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (!user.getId().equals(currentUser.getId())) {
                                    userIds.add(user.getId());
                                }
                            }
                            getSearchBooksResultsHelper(userIds, genre);
                        } else {
                            Log.d("Book", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getSearchBooksResultsHelper(ArrayList<String> userIds, String genre) {
        Query query = db.collection(BOOKS_TABLE)
                .whereIn(BOOKS_TABLE_COL_OWNER_ID, userIds);
        if (!genre.equals("")
                && !genre.equals("Select Genre")
                && !genre.equals("All")
        ) {
            query = query.whereEqualTo(BOOKS_TABLE_COL_GENRE, genre);
        }
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Book> results = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Book book = document.toObject(Book.class);
                                results.add(book);
                            }
                            searchBooksResults = results;
                            updateListViewBooks(searchBooksResults);
                        } else {
                            Log.e("SearchBooksFragment", "Error retrieving books");
                        }
                    }
                });
    }

    private void updateListViewBooks(List<Book> books) {
        ListView listviewUsersBooks = getView().findViewById(R.id.listview_books);
        if (books.size() >= 1) {
            CustomAdapterBooks adapter = new CustomAdapterBooks(getActivity(), books);
            listviewUsersBooks.setOnItemClickListener(listview_books_itemClickListener);
            listviewUsersBooks.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "No books found.", Toast.LENGTH_SHORT).show();
            listviewUsersBooks.setAdapter(null);
        }
    }

    private void sendTradeRequest(String senderId, String receiverId, String bookId) {
        Query query = db.collection(BOOK_REQUESTS_TABLE)
                .whereEqualTo(BOOK_REQUESTS_TABLE_COL_SENDER_ID, senderId)
                .whereEqualTo(BOOK_REQUESTS_TABLE_COL_BOOK_ID, bookId);
        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {
                                sendTradeRequestHelper(senderId, receiverId, bookId);
                            } else {
                                Toast.makeText(getActivity(), "Trade request already sent.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void sendTradeRequestHelper(String senderId, String receiverId, String bookId) {
        db.collection(USERS_TABLE)
                .whereEqualTo(USERS_TABLE_COL_ID, senderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String senderName = (String) document.get(USERS_TABLE_COL_NAME);
                                String senderUni = (String) document.get(USERS_TABLE_COL_UNIVERSITY);

                                Map<String, Object> tradeRequest = new HashMap<>();
                                tradeRequest.put(BOOK_REQUESTS_TABLE_COL_BOOK_ID, bookId);
                                tradeRequest.put(BOOK_REQUESTS_TABLE_COL_SENDER_ID, senderId);
                                tradeRequest.put(BOOK_REQUESTS_TABLE_COL_RECEIVER_ID, receiverId);
                                tradeRequest.put(BOOK_REQUESTS_TABLE_COL_STATUS, BOOK_REQUEST_STATUS_REQUESTED);
                                tradeRequest.put(BOOK_REQUESTS_TABLE_COL_SENDER_NAME, senderName);
                                tradeRequest.put(BOOK_REQUESTS_TABLE_COL_SENDER_UNIVERSITY, senderUni);

                                db.collection(BOOK_REQUESTS_TABLE).document()
                                        .set(tradeRequest)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getActivity(), "Trade request successfully sent.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("Book requests", "Failed to write new trade request");
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}