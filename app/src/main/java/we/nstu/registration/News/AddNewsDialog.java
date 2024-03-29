package we.nstu.registration.News;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import we.nstu.registration.MainActivity;
import we.nstu.registration.R;
import we.nstu.registration.User.User;
import we.nstu.registration.databinding.DialogAddNewsBinding;

public class AddNewsDialog extends DialogFragment {

    private final int GALLERY_REQUEST = 1;
    private Uri uri;
    private DialogAddNewsBinding binding;
    private List<News> newsList;
    private String email;
    private OnNewsAddedListener listener;
    private Context context;
    private FirebaseFirestore database;

    public AddNewsDialog() {
    }

    public void setOnNewsAddedListener(OnNewsAddedListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddNewsBinding.inflate(inflater, container, false);

        database = FirebaseFirestore.getInstance();

        binding.addNewsButton.setOnClickListener(v -> {

            if (binding.newsTitleEditText.getText().toString().isEmpty() || binding.newsDescriptionEditText.getText().toString().isEmpty() || uri == null)
            {
                return;
            }

            email = MainActivity.getEmail(getContext());

            DocumentReference usersReference = database.collection("users").document(email);

            usersReference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);

                            database.collection("schools").document(String.valueOf(user.getSchoolID())).get()
                                    .addOnSuccessListener(ds -> {

                                        String newsJson = ds.get("newsJson").toString();

                                        if(newsJson != "")
                                        {
                                            SchoolNews schoolNews = SchoolNews.newsFromJson(newsJson);
                                            newsList = schoolNews.getNewsList();
                                        }
                                        else
                                        {
                                            newsList = new ArrayList<>();
                                        }

                                        LocalDateTime currentDateTime = LocalDateTime.now();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
                                        String formattedDateTime = currentDateTime.format(formatter);

                                        newsList.add(new News(binding.newsTitleEditText.getText().toString(),binding.newsDescriptionEditText.getText().toString(),formattedDateTime, new ArrayList<String>(),String.valueOf(newsList.size())));

                                        SchoolNews newSchoolNews = new SchoolNews(newsList);
                                        String newNewsJson = newSchoolNews.newsToJson();

                                        database.collection("schools")
                                                .document(String.valueOf(user.getSchoolID()))
                                                .update("newsJson", newNewsJson)
                                                .addOnSuccessListener(runnable -> {
                                                    Toast.makeText(context, "Новость успешно создана", Toast.LENGTH_SHORT).show();
                                                });

                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReference();
                                        StorageReference newsRef = storageRef.child("Schools").child(String.valueOf(user.getSchoolID())).child("News").child(String.valueOf(newsList.size() - 1));

                                        if (uri!=null)
                                        {
                                            StorageReference imageRef = newsRef.child("news_logo.jpg");
                                            imageRef.putFile(uri);
                                        }

                                        if (listener != null) {
                                            listener.onNewsAdded();
                                        }

                                    });
                        }
                    });
            dismiss();
        });

        binding.imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST);
        });



        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            uri = selectedImage;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = (int) (displayMetrics.widthPixels * 0.9);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        return dialog;
    }

    public interface OnNewsAddedListener {
        void onNewsAdded();
    }

}