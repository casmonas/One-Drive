package com.chikakraft.onedrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;

public class NoInternet extends AppCompatActivity {

    Button btnRetry;
    ImageView noInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        btnRetry = findViewById(R.id.button3);
        noInternet = findViewById(R.id.imageView3);
        Glide.with(this).load(R.drawable.nointernet).into(noInternet);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
                if(networkInfo != null){
                    Intent intent = new Intent(NoInternet.this,Home.class);
                    startActivity(intent);
                    finish();
                    Animatoo.animateInAndOut(NoInternet.this);
                }

            }
        });
    }
}