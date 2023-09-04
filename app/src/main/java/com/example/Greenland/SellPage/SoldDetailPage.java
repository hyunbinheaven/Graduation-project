package com.example.Greenland.SellPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.Greenland.ChatRoomActivity;
import com.example.Greenland.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SoldDetailPage extends AppCompatActivity {

    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private TextView productContentsTextView;
    private Button DeleteButton;
    private Button EditButton;
    private String Email;
    private String imageUrl;
    private String productContents;
    private String productPrice;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solddetail);


        // 뷰 요소 초기화
        productImageView = findViewById(R.id.product_image_view);
        productNameTextView = findViewById(R.id.product_name_text_view);
        productPriceTextView = findViewById(R.id.product_price_text_view);
        productContentsTextView = findViewById(R.id.product_contents_text_view);
        DeleteButton = findViewById(R.id.delete_button);
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
                if (currentUserEmail.equals(Email)) {
                    // Navigate to the edit activity
                    Intent intent = new Intent(SoldDetailPage.this, SoldEdit.class);
                    intent.putExtra("productName", productName);
                    intent.putExtra("productPrice", productPrice);
                    intent.putExtra("productContents", productContents);
                    intent.putExtra("imageUrl", imageUrl);
                    intent.putExtra("Email", Email);
                    startActivityForResult(intent, 1);
                } else {
                    //Toast.makeText(SoldDetailPage.this, "니 글이 아닙니다...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentUserEmail.equals(Email)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SoldDetailPage.this);
                    builder.setMessage("게시물을 삭제하시겠습니까?")
                            .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 데이터베이스에서 해당 내용을 삭제하는 작업 수행
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product");
                                    Query query = databaseReference.orderByChild("imageUrl").equalTo(imageUrl).limitToLast(1);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                                                // 이미지 파일 삭제
                                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // 이미지 파일 삭제 성공
                                                        snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                // 삭제 성공
                                                                finish();
                                                            } else {
                                                                // 삭제 실패
                                                                Toast.makeText(SoldDetailPage.this, "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // 이미지 파일 삭제 실패
                                                        Toast.makeText(SoldDetailPage.this, "이미지 파일 삭제 실패", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // 오류 처리
                                            Toast.makeText(SoldDetailPage.this, "데이터베이스 오류: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 취소
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                } else {
                    Toast.makeText(SoldDetailPage.this, "게시물의 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button chatButton = findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserEmail.equals(Email)) {
                    Toast.makeText(SoldDetailPage.this, "자신과는 채팅할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // Navigate to the chat activity
                    Intent intent = new Intent(SoldDetailPage.this, ChatRoomActivity.class);
                    String modifiedEmail = Email.replace(".", "_");
                    intent.putExtra("sellerEmail", modifiedEmail);
                    startActivity(intent);
                }
            }
        });
    }
}