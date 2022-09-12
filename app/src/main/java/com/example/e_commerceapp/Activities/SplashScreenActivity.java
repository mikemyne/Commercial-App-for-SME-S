package com.example.e_commerceapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_commerceapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim, thirdAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activitysplash_screen_main);

        ImageView imageView = findViewById(R.id.logo);
        TextView title = findViewById(R.id.title);
        TextView slogan = findViewById(R.id.slogan);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        thirdAnimation = AnimationUtils.loadAnimation(this,R.anim.third_animation);

        imageView.setAnimation(topAnim);
        title.setAnimation(bottomAnim);
        slogan.setAnimation(thirdAnimation);


        int SPLASH_SCREEN = 3300;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}