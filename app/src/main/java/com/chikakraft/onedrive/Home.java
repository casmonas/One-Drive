package com.chikakraft.onedrive;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.chikakraft.onedrive.fragments.HomeFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.chikakraft.onedrive.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    private SessionManager sessionManager;

    private  DrawerLayout drawer;
    private long backpressed;
    private ImageView menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){
            Intent intent = new Intent(Home.this,NoInternet.class);
            startActivity(intent);
            finish();
            Animatoo.animateInAndOut(this);
        }

        sessionManager = new SessionManager(this);

        menuItem = findViewById(R.id.imageView2);

            ////////////check login///////////////////////

        if(!sessionManager.isLogin()){
            Intent intent = new Intent(Home.this,Login.class);
            startActivity(intent);
            Animatoo.animateSlideUp(this);
            finish();
        }

        ////////////check login///////////////////////

        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
       // NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.nav_home){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_content_home,new HomeFragment()).commit();

                    drawer.close();
                }

                if(item.getItemId()==R.id.nav_settings){

                    Intent intent = new Intent(Home.this,EditUserProfile.class);
                    startActivity(intent);
                    finish();
                    Animatoo.animateSlideRight(Home.this);

                    drawer.close();
                }



                if(item.getItemId()==R.id.nav_switch){

                    Toast.makeText(Home.this, "Switch to Driver", Toast.LENGTH_SHORT).show();
                    drawer.close();
                }

                if(item.getItemId()==R.id.nav_history){

                    Toast.makeText(Home.this, "History", Toast.LENGTH_SHORT).show();
                    drawer.close();
                }

                if(item.getItemId()==R.id.nav_signout){

                    sessionManager.editor.clear();
                    sessionManager.editor.commit();


                    Intent intent = new Intent(Home.this,Login.class);
                    startActivity(intent);
                    finish();
                    Animatoo.animateSlideRight(Home.this);
                    drawer.close();
                }

                return true;
            }
        });

        //////////////////////menu btn//////////////////
        menuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.open();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case  R.id.action_switch:
                Toast.makeText(this, "Switch to driver", Toast.LENGTH_SHORT).show();
                return true;
            case  R.id.action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case  R.id.action_sign_out:
                sessionManager.editor.clear();
                sessionManager.editor.commit();


                Intent intent = new Intent(Home.this,Login.class);
                startActivity(intent);
                finish();
                Animatoo.animateSlideRight(Home.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if(backpressed + 100 > System.currentTimeMillis()){
            super.onBackPressed();
        }else {
            Toast.makeText(this, "Press once again to exit.", Toast.LENGTH_SHORT).show();
        }
        backpressed = System.currentTimeMillis();
    }
}