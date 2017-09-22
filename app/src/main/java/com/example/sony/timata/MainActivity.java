package com.example.sony.timata;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolBar;
    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPager;
    private TabLayout mTabLayout;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tab_pager);
        mSectionPager = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPager);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mAuth = FirebaseAuth.getInstance();
        //For ToolBar
        mToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Timata");

        //------------------------Firebase Database
        if (mAuth.getCurrentUser() != null) {
            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current_user = mAuth.getCurrentUser();
        if(current_user == null){
           sendToStart();
        } else {
            mRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser current_user = mAuth.getCurrentUser();
        if (current_user != null) {
            mRef.child("online").setValue(false);
        }


    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout_btn){

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if(item.getItemId()==R.id.main_settings_btn){
            Intent Settingintent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(Settingintent);
        }
        if(item.getItemId()==R.id.main_Users_btn){
            Intent Usersintent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(Usersintent);
        }


        return true;
    }
}
