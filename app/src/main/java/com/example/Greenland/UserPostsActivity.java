package com.example.Greenland;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Greenland.DB.product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserPostsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private List<product> mUserPostsList;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        mRecyclerView = findViewById(R.id.user_posts_recycler_view);
        mUserPostsList = new ArrayList<>();
        mAdapter = new MyAdapter(this, mUserPostsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 현재 사용자의 이메일 주소 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserEmail = user.getEmail();

        // Firebase Realtime Database에서 사용자의 게시글 가져오기
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserPostsList.clear();
                List<product> tempList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    if (email.equals(currentUserEmail)) {
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                        String productContents = snapshot.child("productcontents").getValue(String.class);
                        String productName = snapshot.child("productname").getValue(String.class);
                        String productPrice = snapshot.child("productprice").getValue(String.class);
                        String datetime = snapshot.child("datetime").getValue(String.class);
                        String nickname = snapshot.child("nickname").getValue(String.class);
                        product product = new product(productName, productPrice, productContents, imageUrl, email, datetime, nickname);
                        tempList.add(product);
                    }
                }
                // 임시 리스트를 역순으로 추가하여 mUserPostsList에 할당
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    mUserPostsList.add(tempList.get(i));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}