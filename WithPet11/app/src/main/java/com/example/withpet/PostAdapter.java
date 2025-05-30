package com.example.withpet;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(Post post);
    }

    private ArrayList<Post> postList;
    private boolean showDelete;
    private OnDeleteClickListener deleteListener;
    private boolean showFavorite;

    private final FavoriteAdd favoriteAddManager = new FavoriteAdd();

    public PostAdapter(ArrayList<Post> postList, boolean showDelete, OnDeleteClickListener deleteListener, boolean showFavorite) {
        this.postList = postList;
        this.showDelete = showDelete;
        this.deleteListener = deleteListener;
        this.showFavorite = showFavorite;
    } // 즐겨찾기를 표시함

    public PostAdapter(ArrayList<Post> postList) {
        this(postList, false, null);
    }

    public PostAdapter(ArrayList<Post> postList, boolean showDelete, OnDeleteClickListener deleteListener) {
        this.postList = postList;
        this.showDelete = showDelete;
        this.deleteListener = deleteListener;
    } //즐겨찾기를 표시하지 않음

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
        holder.tvTitle.setText(post.getTitle());
        holder.tvContent.setText(post.getContent());
        holder.tvNickname.setText(post.getNickname());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetail.class);
            intent.putExtra("title", post.getTitle());
            intent.putExtra("content", post.getContent());
            intent.putExtra("imageUrl", post.getImageUrl());
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

            // 즐겨찾기 추가 기능 → FavoriteAddManager 이용
            holder.btnFavorite.setOnClickListener(v -> {
                favoriteAddManager.addToFavorites(post, v.getContext());
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







