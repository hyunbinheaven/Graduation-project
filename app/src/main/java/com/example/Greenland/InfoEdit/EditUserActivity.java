package com.example.Greenland.InfoEdit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.Greenland.Login.LoginActivity;
import com.example.Greenland.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditUserActivity extends Activity {

    private EditText addressEditText;
    private EditText nickEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("UserList");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        Query query = userRef.orderByChild("Email").equalTo(email);
        Button deleteButton = findViewById(R.id.user_delete_Button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 띄우기
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setMessage("정말로 계정을 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // '아니오' 버튼 클릭 시 다이얼로그 닫기
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    currentUser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("myPageFragment", "User account deleted.");
                                                    }
                                                }
                                            });
                                }
                                // userlist 노드에서 삭제
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userRef = database.getReference("UserList");
                                String uid = currentUser.getUid();
                                userRef.child(uid).removeValue();

                                // chats 노드에서 삭제
                                DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference()
                                        .child("Chats")
                                        .child("User")
                                        .child(currentUser.getEmail().replace(".", "_"));
                                chatDB.removeValue();

                                // product 노드에서 삭제
                                DatabaseReference productRef = database.getReference("product");
                                Query userProductQuery = productRef.orderByChild("email").equalTo(currentUser.getEmail());
                                userProductQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            childSnapshot.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                    }
                                });
                                // farms 노드에서 삭제
                                DatabaseReference farmsRef = database.getReference("farms");
                                Query userFarmsQuery = farmsRef.orderByChild("email").equalTo(currentUser.getEmail());
                                userFarmsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            childSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                    }
                                });

                                // 로그아웃 처리
                                mAuth.signOut();
                                Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        addressEditText = findViewById(R.id.AddressEditText);
        nickEditText = findViewById(R.id.nickEditText);
        Button editUserSuccessButton = findViewById(R.id.edit_user_success);

        editUserSuccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newAddress = addressEditText.getText().toString();
                String newNickname= nickEditText.getText().toString();

                // 닉네임 수정
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String key = childSnapshot.getKey();
                                userRef.child(key).child("Nickname").setValue(newNickname);
                                userRef.child(key).child("Address").setValue(newAddress);
                                Toast.makeText(EditUserActivity.this, "닉네임과 주소가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}