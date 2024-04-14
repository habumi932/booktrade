package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_BOOK_ID;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE;
import static com.hangbui.booktrade.Constants.BOOK_REQUESTS_TABLE_COL_RECEIVER_ID;
import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
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
                            updateListviewBooks(books);
                        } else {
                            Log.e("Book", "Error retrieving book info");
                        }
                    }
                });
    }

    private void updateListviewBooks(List<Book> books) {
        View view = getView();
        CustomAdapterBooks adapter = new CustomAdapterBooks(getActivity(), books);
        ListView listviewTradeRequests = view.findViewById(R.id.listview_trade_requests);
        // listviewBooks.setOnItemClickListener(listview_trade_);
        listviewTradeRequests.setAdapter(adapter);
    }
}