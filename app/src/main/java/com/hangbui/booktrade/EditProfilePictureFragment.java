package com.hangbui.booktrade;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfilePictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfilePictureFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfilePictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfilePictureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfilePictureFragment newInstance(String param1, String param2) {
        EditProfilePictureFragment fragment = new EditProfilePictureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // LISTENERS
    private View.OnClickListener button_choose_photo_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
    private View.OnClickListener button_save_changes_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener button_save_photo_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button buttonChoosePhoto = view.findViewById(R.id.button_choose_photo);
        Button buttonSaveChanges = view.findViewById(R.id.button_save_changes);
        buttonChoosePhoto.setOnClickListener(button_choose_photo_clickListener);
        buttonSaveChanges.setOnClickListener(button_save_changes_clickListener);
    }
}