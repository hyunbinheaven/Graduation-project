package com.example.Greenland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.Greenland.BottomNavi.ChatFragment;
import com.example.Greenland.BottomNavi.FarmFragment;
import com.example.Greenland.BottomNavi.GraphFragment;
import com.example.Greenland.BottomNavi.HomeFragment;
import com.example.Greenland.BottomNavi.myPageFragment;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    HomeFragment homeFragment;
    ChatFragment chatFragment;
    GraphFragment graphFragment;
    myPageFragment myPageFragment;
    FarmFragment farmFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        homeFragment = new HomeFragment();
        chatFragment = new ChatFragment();
        myPageFragment = new myPageFragment();
        graphFragment = new GraphFragment();
        farmFragment = new FarmFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, homeFragment).commit();

        NavigationBarView navigationBarView = findViewById(R.id.bottomNavi);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, homeFragment).commit();
                        return true;
                    case R.id.graph:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, graphFragment).commit();
                        return true;
                    case R.id.chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, chatFragment).commit();
                        return true;
                    case R.id.myPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, myPageFragment).commit();
                        return true;
                    case R.id.farm:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, farmFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}