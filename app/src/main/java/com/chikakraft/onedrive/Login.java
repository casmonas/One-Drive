package com.chikakraft.onedrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;


public class Login extends AppCompatActivity {

    private Button btnPhone, btnGoogle;
    Animation phoneAnimation,googleAnimation;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnPhone = findViewById(R.id.button2);
        btnGoogle = findViewById(R.id.button);


        ////////////check login///////////////////////
            sessionManager = new SessionManager(this);

            if(sessionManager.isLogin()){
                Intent intent = new Intent(Login.this,Home.class);
                startActivity(intent);
                finish();
                Animatoo.animateSlideUp(this);
            }

        ////////////check login///////////////////////

        phoneAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_anim);
        googleAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_anim);

        btnPhone.setAnimation(phoneAnimation);
        btnGoogle.setAnimation(googleAnimation);
    }

    public void phoneLoginClick(View view) {

        Intent intent = new Intent(Login.this,PhoneLogin.class);
        startActivity(intent);
        finish();
        Animatoo.animateSlideUp(this);

    }
}