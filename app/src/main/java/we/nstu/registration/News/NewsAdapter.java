package we.nstu.registration.News;


import static we.nstu.registration.R.drawable.add_photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import we.nstu.registration.MainActivity;
import we.nstu.registration.R;
import we.nstu.registration.User.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> implements ItemTouchHelperAdapter{



    private List<News> newsList;
    private OnItemClickListener listener;
    private FirebaseFirestore database;

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public List<News> getNewsList()
    {
        return newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        database = FirebaseFirestore.getInstance();

        News news = newsList.get(position);
        holder.newsTitle.setText(news.getNewsTitle());
        holder.newsTime.setText(news.getNewsTime());


        String email = MainActivity.getEmail(holder.imageView.getContext());

        DocumentReference usersReference = database.collection("users").document(email);

        usersReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists())
                    {
                        User user = documentSnapshot.toObject(User.class);

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference imageRef = storageRef.child("Schools").child(String.valueOf(user.getSchoolID())).child("News").child(String.valueOf(news.getNewsID())).child("news_logo.jpg");

                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            Glide.with(holder.itemView)
                                    .load(uri)
                                    .into(holder.imageView);

                        });
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(news);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle, newsTime;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.textTitle);
            newsTime = itemView.findViewById(R.id.timeAndDate);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(News news);
    }
    public void removeItem(int position) {
        newsList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public void onItemDismiss(int position) {
        newsList.remove(position);
        notifyItemRemoved(position);
    }
}

