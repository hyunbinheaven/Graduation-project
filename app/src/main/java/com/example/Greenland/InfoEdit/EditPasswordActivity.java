package com.example.Greenland.InfoEdit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.Greenland.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class EditPasswordActivity extends Activity {

    private EditText passwordEditText;
    private EditText pwConfirmationEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();

        passwordEditText = findViewById(R.id.passwordEditText);
        pwConfirmationEditText = findViewById(R.id.pwConfirmationEditText);
        Button editUserSuccessButton = findViewById(R.id.edit_user_success);

        editUserSuccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 중복 클릭 방지
                if (editUserSuccessButton.isClickable()) {
                    editUserSuccessButton.setClickable(false);
                    // 비밀번호 수정
                    String newPassword = passwordEditText.getText().toString();
                    String confirmPassword = pwConfirmationEditText.getText().toString();

                    if (newPassword.equals(confirmPassword)) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // 중복 클릭 방지 해제
                                            editUserSuccessButton.setClickable(true);
                                            if (task.isSuccessful()) {
                                                finish();
                                                Toast.makeText(EditPasswordActivity.this, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // 오류 처리
                                                //Log.d("activity_edit_user", "Password update failed: " + task.getException().getMessage());
                                                //Toast.makeText(EditPasswordActivity.this, "Password update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        // 비밀번호가 일치하지 않을 때
                        Toast.makeText(EditPasswordActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        // 중복 클릭 방지 해제
                        editUserSuccessButton.setClickable(true);
                    }
                }
            }
        });
    }
}
