package com.example.Greenland.Farm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Greenland.DB.product;
import com.example.Greenland.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FarmDetailActivity extends AppCompatActivity {

    private TextView farmNameTextView;
    private TextView farmDescriptionTextView;
    private RecyclerView recyclerView;
    private productAdapter adapter;
    private List<product> productList;
    private String farmEmail;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_detail);

        farmNameTextView = findViewById(R.id.farm_name_text_view);
        farmDescriptionTextView = findViewById(R.id.farm_description_text_view);
        recyclerView = findViewById(R.id.farm_product_recycler_view);
        editButton = findViewById(R.id.edit_button);

        productList = new ArrayList<>();
        adapter = new productAdapter(productList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 농장 정보 받아오기
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String farmName = extras.getString("farmName");
            String farmDescription = extras.getString("farmDescription");
            farmEmail = extras.getString("farmEmail");
            String farmImageUrl = extras.getString("farmImageUrl");

            farmNameTextView.setText(farmName);
            farmDescriptionTextView.setText(farmDescription);
            // TODO: 농장 프로필 이미지 설정 (farmImageUrl 사용)

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product");
            Query query = databaseReference.orderByChild("email").equalTo(farmEmail);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    productList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        product product = snapshot.getValue(product.class);
                        productList.add(product);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Error handling
                }
            });
        }


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmDetailActivity.this, FarmEditActivity.class);
                intent.putExtra("farmEmail", farmEmail);
                startActivity(intent);
            }
        });
    }
}