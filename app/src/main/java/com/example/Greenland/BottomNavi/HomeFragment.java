package com.example.Greenland.BottomNavi;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Greenland.DB.product;
import com.example.Greenland.MyAdapter;
import com.example.Greenland.SellPage.PicActivity;
import com.example.Greenland.R;
import com.example.Greenland.UserPostsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private List<product> mProductList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FloatingActionButton sell_button = view.findViewById(R.id.sell_btn);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mProductList = new ArrayList<>();
        mAdapter = new MyAdapter(getContext(), mProductList);
        mRecyclerView.setAdapter(mAdapter);
        // Layout Manager 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product");
        FloatingActionButton searchBtn = view.findViewById(R.id.search_btn);
        SearchView searchView = view.findViewById(R.id.search_edit_text);
        searchView.setQueryHint("검색어를 입력하세요");
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검색어 가져오기
                String query = searchView.getQuery().toString();

                // 필터링된 결과를 담을 임시 리스트
                List<product> filteredList = new ArrayList<>();

                // 데이터베이스에서 필터링된 결과 가져오기
                for (product p : mProductList) {
                    // 필터링 조건을 여기에 추가 (예: 제품 이름으로 필터링)
                    if (p.getproductname().contains(query)) {
                        filteredList.add(p);
                    }
                }

                // 어댑터에 필터링된 결과 설정 및 갱신
                mAdapter = new MyAdapter(getContext(), filteredList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProductList.clear();
                List<product> tempList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String productContents = snapshot.child("productcontents").getValue(String.class);
                    String productName = snapshot.child("productname").getValue(String.class);
                    String productPrice = snapshot.child("productprice").getValue(String.class);
                    String datetime = snapshot.child("datetime").getValue(String.class);
                    String nickname = snapshot.child("nickname").getValue(String.class);
                    // 가져온 데이터를 임시 리스트에 추가
                    product product = new product(productName, productPrice, productContents, imageUrl, email, datetime, nickname);
                    tempList.add(product);
                }
                // 임시 리스트를 역순으로 추가하여 mProductList에 할당
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    mProductList.add(tempList.get(i));
                }
                mAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PicActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton userPostsButton = view.findViewById(R.id.user_posts_button);
        userPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserPostsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}