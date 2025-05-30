package com.example.withpet;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import android.support.design.widget.BottomNavigationView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<Post> postList;
    private DatabaseReference postRef;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // XML 이름 맞게 수정

        recyclerView = findViewById(R.id.recycler_posts);
        fabAdd = findViewById(R.id.fab_add_post);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        adapter = new PostAdapter(postList, false, null, true);  // 삭제 X, 즐겨찾기 O

        recyclerView.setAdapter(adapter);

        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        // 게시글 실시간 로딩
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setKey(dataSnapshot.getKey());
                        postList.add(0, post);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 예외 처리
            }
        });

        // 글쓰기 버튼 클릭
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Createpost.class));
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavi);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_home) {
                return true;
            } else if (id == R.id.action_my) {
                startActivity(new Intent(MainActivity.this, MyPostsActivity.class));
                return true;
            } else if (id == R.id.action_favorites) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                return true;
            }

            return false;
        });


    }
}
