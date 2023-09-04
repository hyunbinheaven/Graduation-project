package com.example.Greenland.Login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Greenland.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextNickname;
    private Button buttonJoin;
    private EditText editTextAddress;
    private EditText editTextPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        editTextNickname = (EditText) findViewById(R.id.editText_nickname);
        editTextAddress = (EditText) findViewById(R.id.editText_address);
        editTextPhoneNumber = (EditText) findViewById(R.id.editText_phoneNumber);
        buttonJoin = (Button) findViewById(R.id.btn_join);
        // 입력된 문자열이 3, 8번째 자리일 때 하이픈 추가
        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            private boolean formatting;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 1 && !formatting) {
                    if (start == 2 || start == 7) {
                        formatting = true;
                        editTextPhoneNumber.setText(s + "-");
                        editTextPhoneNumber.setSelection(s.length() + 1); // 커서 위치 조정
                    }
                }
                formatting = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    // 이메일과 비밀번호가 공백이 아닌 경우
                    createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextNickname.getText().toString(),
                            editTextAddress.getText().toString(), editTextPhoneNumber.getText().toString());
                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(SignUpActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }

            }

        });
    }

    private void createUser(String email, String password, String nickname, String address, String phoneNumber) {
        // Firebase Authentication을 이용해 사용자 생성
        buttonJoin.setClickable(false);  // disable the button
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        buttonJoin.setClickable(true);
                        if (task.isSuccessful()) {
                            // Firebase Realtime Database를 이용해 사용자 정보 저장
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference("UserList");
                            userListRef.child(uid).child("Email").setValue(email);
                            userListRef.child(uid).child("Nickname").setValue(nickname);
                            userListRef.child(uid).child("Address").setValue(address);
                            userListRef.child(uid).child("PhoneNumber").setValue(phoneNumber);
                            finish();
                            Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                        } else {
                            // 계정이 중복된 경우나 비밀번호 제한
                            Toast.makeText(SignUpActivity.this, "계정이 중복되었거나 5자리 이하의 비밀번호를 입력했습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}