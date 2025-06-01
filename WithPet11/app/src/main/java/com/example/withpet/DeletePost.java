package com.example.withpet;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeletePost {
    private final DatabaseReference postRef;

    public DeletePost() {
        postRef = FirebaseDatabase.getInstance().getReference("Post");
    }

    public void deletePost(String postKey, Runnable onSuccess, Runnable onFailure) {
        postRef.child(postKey).removeValue()
                .addOnSuccessListener(unused -> onSuccess.run())
                .addOnFailureListener(e -> onFailure.run());
    }
}

