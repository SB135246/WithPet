package com.example.withpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<Post> myPosts;
    private DatabaseReference postRef;
    private String uid;
    private FirebaseAuth mAuth;
    private ImageView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        recyclerView = findViewById(R.id.recycler_my_posts);
        btnLogout = findViewById(R.id.btn_logout);
        TextView tvNickname = findViewById(R.id.tv_nickname);

        myPosts = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("WithPet")
                .child("UserAccount")
                .child(uid)
                .child("nickname");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nickname = snapshot.getValue(String.class);
                    tvNickname.setText(nickname + " 님");
                } else {
                    tvNickname.setText("사용자");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        adapter = new PostAdapter(myPosts, true, post -> {
            postRef.child(post.key).removeValue();
            myPosts.remove(post);
            adapter.notifyDataSetChanged();
        }, false); // 즐겨찾기 버튼은 안 보이게 설정

        recyclerView.setAdapter(adapter);

        postRef.orderByChild("author").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPosts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.key = postSnapshot.getKey(); // 키 저장
                        myPosts.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MyPostsActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}
