package com.hangbui.booktrade;

import static com.hangbui.booktrade.Constants.BOOKS_TABLE;
import static com.hangbui.booktrade.Constants.BOOKS_TABLE_COL_OWNER_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewUserFragment extends Fragment {

    private static final String ARG_USER = "user";

    private User theUser;
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

        if(theUser.getPhotoUrl().equals("")) {
            imageViewPhoto.setImageResource(R.drawable.default_profile_pic);
        } else {
            // TODO: Implement displaying user profile pic
        }
        textViewName.setText(theUser.getName());
        textViewUniversity.setText(theUser.getUniversity());
        getTheUsersBooks(theUser.getId());
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
}