package com.example.Greenland.Login;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.Greenland.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SearchPassword extends Activity {

    private EditText emailInput;
    private Button sendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_search);

        emailInput = findViewById(R.id.Email_input);
        sendEmailButton = findViewById(R.id.edit_user_success);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SearchPassword.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SearchPassword.this, "비밀번호 변경 이메일이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        throw task.getException();
                                    }  catch (Exception e) {
                                        Toast.makeText(SearchPassword.this, "비밀번호 변경 이메일을 전송하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}