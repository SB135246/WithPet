package com.example.withpet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Createpost extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText etTitle, etContent;
    private Button btnSubmit;
    private ImageView etPreview;

    private FirebaseAuth mAuth;
    private DatabaseReference postRef;
    private StorageReference storageRef;

    private Uri photoUri;
    private String imageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        etPreview = findViewById(R.id.et_preview);
        btnSubmit = findViewById(R.id.et_submit);
        Button btnCamera = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
        storageRef = FirebaseStorage.getInstance().getReference("PostImages");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        btnCamera.setOnClickListener(v -> launchCamera());

        btnSubmit.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        btnSubmit.setEnabled(false);

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(true);
            return;
        }

        if (photoUri != null) {
            StorageReference imgRef = storageRef.child(System.currentTimeMillis() + ".jpg");
            imgRef.putFile(photoUri).addOnSuccessListener(taskSnapshot ->
                    imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        uploadPostWithNickname(title, content, uid, imageUrl);
                    })
            ).addOnFailureListener(e -> {
                Toast.makeText(this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            });
        } else {
            uploadPostWithNickname(title, content, uid, null);
        }
    }

    private void uploadPostWithNickname(String title, String content, String uid, String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("WithPet/UserAccount").child(uid);

        userRef.get().addOnSuccessListener(snapshot -> {
            String nickname = snapshot.child("nickname").getValue(String.class);

            String key = postRef.push().getKey();
            Post post = new Post(title, content, uid, System.currentTimeMillis(), imageUrl);
            post.setNickname(nickname);

            postRef.child(key).setValue(post).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "게시글 등록 실패", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                }
            });
        });
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException e) {
                Toast.makeText(this, "파일 생성 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && photoUri != null) {
            etPreview.setImageURI(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "카메라 권한 허용됨", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}



