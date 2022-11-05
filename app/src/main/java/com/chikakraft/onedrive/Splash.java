package com.chikakraft.onedrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Splash extends AppCompatActivity {

    private TextView animatetext;
    private ImageView marker;
    Animation animationNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

      new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,Login.class);
                startActivity(intent);
                finish();
            }
        },5500);

        init();
    }

    private void init(){
        animatetext = findViewById(R.id.textView);
        marker = findViewById(R.id.imageView);
        animationNow = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_anim);
        animatetext.setAnimation(animationNow);

        Glide.with(this).load(R.drawable.marker).into(marker);

    }
}