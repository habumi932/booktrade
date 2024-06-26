package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_BOOK_ID;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_OWNER_ID;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_RECEIVER_ID;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TradeRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TradeRequestsFragment extends Fragment {

    private FirebaseFirestore db;
    private User currentUser;
    private List<Book> tradeRequestBooks;
    private List<TradeRequest> tradeRequests;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TradeRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TradeRequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TradeRequestsFragment newInstance(String param1, String param2) {
        TradeRequestsFragment fragment = new TradeRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
        currentUser = getActivity().getIntent().getParcelableExtra(EXTRA_CURRENT_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trade_requests, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getTradeRequests(currentUser.getId());
    }

    // LISTENERS
    private AdapterView.OnItemClickListener listview_trade_request_itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
            TradeRequest request = tradeRequests.get(position);
            Book thisBook = tradeRequestBooks.get(position);
            String message = request.getSenderName() + " from " + request.getSenderUniversity()
                    + " requested to borrow this book.";
            myBuilder
                    .setTitle("Book trade request")
                    .setMessage(message)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            acceptTradeRequest(request, thisBook);
                        }
                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            declineTradeRequest(request);
                        }
                    });
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
        }
    };

    private void getTradeRequests(String uid) {
        db.collection(BOOK_REQUESTS_TABLE)
                .whereEqualTo(BOOK_REQUESTS_TABLE_COL_RECEIVER_ID, uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<TradeRequest> requests = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TradeRequest request = document.toObject(TradeRequest.class);
                                request.setRequestId(document.getId());
                                requests.add(request);
                            }
                            updateTradeRequestsList(requests);
                        } else {
                            Log.e("TradeRequest", "Error retrieving rtrade requests");
                        }
                    }
                });
    }

    private void updateTradeRequestsList(ArrayList<TradeRequest> requests) {
        if(requests.isEmpty()) {
            View view = getView();
            TextView textViewMessage = view.findViewById(R.id.textView_no_trade_requests_message);
            textViewMessage.setText(R.string.message_no_trade_requests);
            return;
        }
        tradeRequests = requests;
        ArrayList<String> bookIds = new ArrayList<>();
        for (TradeRequest request : requests) {
            bookIds.add(request.getBookId());
        }

        db.collection(BOOKS_TABLE)
                .whereIn(BOOKS_TABLE_COL_BOOK_ID, bookIds)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            ArrayList<Book> books = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                Book theBook = document.toObject(Book.class);
                                books.add(theBook);
                            }
                            updateListviewTradeRequestBooks(books);
                        } else {
                            Log.e("Book", "Error retrieving book info");
                        }
                    }
                });
    }

    private void updateListviewTradeRequestBooks(List<Book> books) {
        tradeRequestBooks = books;
        View view = getView();
        CustomAdapterBooks adapter = new CustomAdapterBooks(getActivity(), books);
        ListView listviewTradeRequests = view.findViewById(R.id.listview_trade_requests);
        listviewTradeRequests.setOnItemClickListener(listview_trade_request_itemClickListener);
        listviewTradeRequests.setAdapter(adapter);
    }

    private void acceptTradeRequest(TradeRequest request, Book book) {
        String newOwnerId = request.getSenderId();
        String bookId = book.getBookId();
        tradeRequests.remove(request);
        tradeRequestBooks.remove(book);

        db.collection(BOOKS_TABLE)
                .document(bookId)
                .update(BOOKS_TABLE_COL_OWNER_ID, newOwnerId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            deleteTradeRequest(request, book);
                        } else {
                            Log.e("Book", "Error updating new owner id in books table");
                        }
                    }
                });
    }

    private void deleteTradeRequest(TradeRequest request, Book book) {
        String requestId = request.getRequestId();
        db.collection(BOOK_REQUESTS_TABLE)
                .document(requestId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(
                                getActivity(),
                                "Book trade request accepted, book removed from your profile",
                                Toast.LENGTH_SHORT).show();
                        replaceFragment(BooksFragment.newInstanceRemoveBook(book));
                    }
                });
    }

    private void declineTradeRequest(TradeRequest request) {
        String requestId = request.getRequestId();
        db.collection(BOOK_REQUESTS_TABLE)
                .document(requestId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(
                                getActivity(),
                                "Book trade request declined.",
                                Toast.LENGTH_SHORT).show();
                        getTradeRequests(currentUser.getId());
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
