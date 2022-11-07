package com.chikakraft.onedrive;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.chikakraft.onedrive.services.Constants;
import com.chikakraft.onedrive.services.LocationService;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.chikakraft.onedrive.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
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
        if(backpressed + 1000 > System.currentTimeMillis()){
            super.onBackPressed();
        }else {
            Toast.makeText(this, "Press once again to exit.", Toast.LENGTH_SHORT).show();
        }
        backpressed = System.currentTimeMillis();
    }

    private boolean isLocalServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager!=null){
            for(ActivityManager.RunningServiceInfo serviceInfo: activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(serviceInfo.service.getClassName())){
                    if(serviceInfo.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    Home.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        }else{
            startLocationService();
        }
    }

    private void startLocationService() {
        if(!isLocalServiceRunning()){
            Intent intent = new Intent(getApplicationContext(),LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
        }
    }

    private void stopLocationService() {
        if(isLocalServiceRunning()){
            Intent intent = new Intent(getApplicationContext(),LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            stopService(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopLocationService();
    }
}