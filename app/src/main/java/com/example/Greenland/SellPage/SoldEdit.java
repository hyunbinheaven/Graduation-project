package com.example.Greenland.SellPage;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.Greenland.BottomNavi.HomeFragment;
import com.example.Greenland.HomeActivity;
import com.example.Greenland.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class SoldEdit extends AppCompatActivity {

    private ImageView productImageView;
    private EditText productNameTextView;
    private EditText productPriceTextView;
    private EditText productContentsTextView;
    private Button EditButton;
    private String Email;
    private String imageUrl;
    private String productContents;
    private String productPrice;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soldedit);
              // 뷰 요소 초기화
        productImageView = findViewById(R.id.product_image_view);
        productNameTextView = findViewById(R.id.product_name_edit_text);
        productPriceTextView = findViewById(R.id.product_price_edit_view);
        productContentsTextView = findViewById(R.id.product_contents_edit_view);
        EditButton = findViewById(R.id.edit_button);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserEmail = user.getEmail();

        // 전달받은 제품 정보 표시
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("productName");
            productPrice = intent.getStringExtra("productPrice");
            productContents = intent.getStringExtra("productContents");
            imageUrl = intent.getStringExtra("imageUrl");
            Email = intent.getStringExtra("Email");
            productNameTextView.setText(productName);
            productPriceTextView.setText(productPrice);
            productContentsTextView.setText(productContents);
            Glide.with(this).load(imageUrl).into(productImageView);
        }

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditButton.setEnabled(false);
                if (currentUserEmail.equals(Email)) {
                    String updatedProductName = productNameTextView.getText().toString();
                    String updatedProductPrice = productPriceTextView.getText().toString();
                    String updatedProductContents = productContentsTextView.getText().toString();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product");
                    Query query = databaseReference.orderByChild("imageUrl").equalTo(imageUrl).limitToLast(1);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("productname").setValue(updatedProductName);
                                snapshot.getRef().child("productprice").setValue(updatedProductPrice);
                                snapshot.getRef().child("productcontents").setValue(updatedProductContents);
                            }
                            Toast.makeText(SoldEdit.this, "수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SoldEdit.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                    });
                } else {
                    Toast.makeText(SoldEdit.this, "수정 실패", Toast.LENGTH_SHORT).show();
                }
                EditButton.setEnabled(true);
            }
        });
    }
}