package com.example.withpet;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PostDetail extends AppCompatActivity {

    private TextView tvDetailTitle, tvDetailContent;
    private ImageView ivDetailImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvDetailTitle = findViewById(R.id.tv_detail_title);
        tvDetailContent = findViewById(R.id.tv_detail_content);
        ivDetailImage = findViewById(R.id.iv_detail_image); // 🔥 이미지뷰 연결

        // 🔽 인텐트로 전달받은 데이터
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        tvDetailTitle.setText(title);
        tvDetailContent.setText(content);

        // 🔥 이미지 URL이 있을 경우에만 Glide로 로드
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(ivDetailImage);
        } else {
            ivDetailImage.setVisibility(View.GONE); // 이미지 없으면 숨김
        }
    }
}

