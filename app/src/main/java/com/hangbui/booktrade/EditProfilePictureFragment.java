package com.hangbui.booktrade;

import static android.app.Activity.RESULT_OK;

import static com.hangbui.booktrade.Constants.EXTRA_CURRENT_USER;
import static com.hangbui.booktrade.Constants.USERS_TABLE;
import static com.hangbui.booktrade.Constants.USERS_TABLE_COL_PHOTO_URL;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfilePictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfilePictureFragment extends Fragment {

    private static final int REQUEST_PICK_IMAGE = 1;
    private FirebaseFirestore db;

    private User currentUser;
    private Uri imageUri;
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
            openGallery();
        }
    };
    private View.OnClickListener button_save_changes_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            updateImage(imageUri);
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
        imageUri = null;
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

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ImageView imageviewPhoto = getView().findViewById(R.id.imageView_edit_photo);
            imageviewPhoto.setImageURI(selectedImageUri);
            imageUri = selectedImageUri;
        }
    }

    private void updateImage(Uri selectedImageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + selectedImageUri.getLastPathSegment());

        UploadTask uploadTask = imageRef.putFile(selectedImageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        String imageUrl = downloadUri.toString();
                        updatePhotoUrlInUsersTable(currentUser.getId(), imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Unsuccessful upload, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePhotoUrlInUsersTable(String uid, String photoUrl) {
        db.collection(USERS_TABLE)
                .document(uid)
                .update(USERS_TABLE_COL_PHOTO_URL, photoUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Photo successfully saved.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}