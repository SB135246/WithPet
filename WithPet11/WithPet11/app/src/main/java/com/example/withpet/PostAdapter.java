package com.example.withpet;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {


    public interface OnDeleteClickListener {
        void onDelete(Post post);
    }

    private ArrayList<Post> postList;
    private boolean showDelete;
    private OnDeleteClickListener deleteListener;
    private boolean showFavorite;

    public PostAdapter(ArrayList<Post> postList, boolean showDelete, OnDeleteClickListener deleteListener, boolean showFavorite) {
        this.postList = postList;
        this.showDelete = showDelete;
        this.deleteListener = deleteListener;
        this.showFavorite = showFavorite;
    }


    public PostAdapter(ArrayList<Post> postList) {
        this(postList, false, null);
    }

    public PostAdapter(ArrayList<Post> postList, boolean showDelete, OnDeleteClickListener deleteListener) {
        this.postList = postList;
        this.showDelete = showDelete;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.title);
        holder.tvContent.setText(post.content);
        holder.tvNickname.setText(post.nickname);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetail.class);
            intent.putExtra("title", post.title);
            intent.putExtra("content", post.content);
            intent.putExtra("imageUrl", post.imageUrl);
            v.getContext().startActivity(intent);
        });

        if (showDelete) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnFavorite.setVisibility(View.GONE); // 삭제화면에선 즐겨찾기 숨김
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(post);
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnFavorite.setVisibility(View.VISIBLE); // 홈화면에선 즐겨찾기 보임

            holder.btnFavorite.setOnClickListener(v -> {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(uid);

                // 이미 즐겨찾기 된 글인지 확인
                favRef.child(post.key).get().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(v.getContext(), "이미 즐겨찾기에 등록된 글입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        favRef.child(post.key).setValue(post); // 여기 수정
                        Toast.makeText(v.getContext(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvNickname;
        ImageView btnDelete, btnFavorite;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}






