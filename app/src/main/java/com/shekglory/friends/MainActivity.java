package com.shekglory.friends;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private Toolbar mToolBar;

    private ViewPager mViewPager;

    private SectionPagerAdapter mSectionPagerAdapter;

    private TabLayout mTabLayout;

    private DatabaseReference mUserRef;
    public static final String CHANNEL_ID = "friendRequestIdCleanChat";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        try {
            Badges.setBadge(getApplicationContext(), 5);
        } catch (BadgesNotSupportedException badgesNotSupportedException) {
            Log.d("notification count", badgesNotSupportedException.getMessage());
        }


        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());



        mViewPager.setAdapter(mSectionPagerAdapter);
//        setContentView(R.id.fragment_request_id);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        checkUser();
        createNotificationChannel();


//
    }

    public void checkUser(){

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if( currentUser == null){
            sendToStart();

        } else{

            mUserRef.child("online").setValue("true");

        }
    }



    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if( currentUser == null){
            sendToStart();

        }

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if( currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if( item.getItemId() == R.id.mainLogOutB){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId() == R.id.settingsId){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if (item.getItemId() == R.id.allusersId){
            Intent allUserList = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(allUserList);
        }


        return true;
    }


    private void createNotificationChannel(){

        //create the notificationchannel, but ony on API 26+ because
        //the notificaionchannel class is new and not in the support library

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String name = "friendRequest";
            String description = "connect friends";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //Register the channel with the system; you can't change the importance
            //or other notification behaviors after this

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }




}
