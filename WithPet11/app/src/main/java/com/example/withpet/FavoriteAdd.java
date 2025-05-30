package com.example.withpet;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavoriteAdd {
    private final DatabaseReference favRef;
    private final String uid;

    public FavoriteAdd() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(uid);
    }

    public void addToFavorites(Post post, Context context) {
        favRef.child(post.getKey()).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Toast.makeText(context, "이미 즐겨찾기에 등록된 글입니다.", Toast.LENGTH_SHORT).show();
            } else {
                favRef.child(post.getKey()).setValue(post);
                Toast.makeText(context, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

