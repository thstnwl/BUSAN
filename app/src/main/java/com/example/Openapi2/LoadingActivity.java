package com.example.Openapi2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    private ImageView imageView1, imageView2, imageView3, imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        // 이미지뷰 초기화
        imageView1 = findViewById(R.id.bus);
        imageView2 = findViewById(R.id.an);
        imageView3 = findViewById(R.id.line);
        imageView4 = findViewById(R.id.buslogo);

        // 이미지뷰를 invisible 상태로 설정
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);

        // anim1, anim2 적용
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation anim1 = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.translatelu);
                Animation anim2 = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.translaterd);
                imageView1.startAnimation(anim1);
                imageView2.startAnimation(anim2);
            }
        }, 1000); // 2초 후에 anim3 실행



        // anim3, anim4 적용
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation anim3 = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.slide_in_right);
                Animation anim4 = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.slide_in_right2);
                imageView3.startAnimation(anim3);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.startAnimation(anim4);
                imageView4.setVisibility(View.VISIBLE);
            }
        }, 2500); // 2초 후에 anim3 실행

        // mainactivity로 이동
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 이전 액티비티 종료
            }
        }, 5000); // 4초 후에 MainActivity로 이동
    }
}
