package com.example.withpet;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavoriteDelete {
    private final DatabaseReference favRef;
    private final String uid;

    public FavoriteDelete() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(uid);
    }

    public void removeFromFavorites(String postKey, Context context, Runnable onComplete) {
        favRef.child(postKey).removeValue().addOnCompleteListener(task -> {
            Toast.makeText(context, "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            onComplete.run();
        });
    }
}

