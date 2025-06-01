package com.example.withpet;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<Post> favoriteList;
    private DatabaseReference favRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites); // 이 XML 파일도 같이 만들어야 해

        recyclerView = findViewById(R.id.recycler_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteList = new ArrayList<>();
        adapter = new PostAdapter(favoriteList, true, post -> {
            favRef.child(post.key).removeValue();
            favoriteList.remove(post);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(uid);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.key = dataSnapshot.getKey();
                        favoriteList.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
