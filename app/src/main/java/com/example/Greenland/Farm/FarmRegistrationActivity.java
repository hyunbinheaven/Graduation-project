package com.example.Greenland.Farm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Greenland.DB.Farm;
import com.example.Greenland.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class FarmRegistrationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText farmNameEditText;
    private EditText farmDescriptionEditText;
    private Button registerButton;
    private Button selectImageButton;
    private ImageView farmImageView;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_registration);

        databaseReference = FirebaseDatabase.getInstance().getReference("farms");
        storageReference = FirebaseStorage.getInstance().getReference("farm_images");

        farmNameEditText = findViewById(R.id.farm_name_edit_text);
        farmDescriptionEditText = findViewById(R.id.farm_description_edit_text);
        registerButton = findViewById(R.id.register_button);
        selectImageButton = findViewById(R.id.select_image_button);
        farmImageView = findViewById(R.id.farm_image_view);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 비활성화
                registerButton.setEnabled(false);

                String farmName = farmNameEditText.getText().toString().trim();
                String farmDescription = farmDescriptionEditText.getText().toString().trim();

                if (farmName.isEmpty() || farmDescription.isEmpty()) {
                    Toast.makeText(FarmRegistrationActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    // 버튼 활성화
                    registerButton.setEnabled(true);
                } else {
                    registerFarm(farmName, farmDescription);
                }
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            farmImageView.setImageURI(imageUri);
        }
    }

    private void registerFarm(String farmName, String farmDescription) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(FarmRegistrationActivity.this, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String farmEmail = user.getEmail();
        String farmId = farmEmail.replace(".", "_"); // '.'을 '_'로 대체하여 유효한 경로로 변환

        if (imageUri != null) {
            try {

                int targetWidth = 800;
                int targetHeight = 800;
                // 이미지 크기 조정
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

                // Firebase Storage에 이미지 업로드
                StorageReference imageRef = storageReference.child(UUID.randomUUID().toString() + ".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();
                UploadTask uploadTask = imageRef.putBytes(imageData);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // 이미지 업로드 성공 시 Firebase Realtime Database에 농장 정보 등록
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String farmImageUrl = uri.toString();

                                        // Firebase Realtime Database에 농장 정보 등록
                                        Farm farm = new Farm(farmEmail, farmName, farmDescription, farmImageUrl);
                                        databaseReference.child(farmId).setValue(farm);
                                        Toast.makeText(FarmRegistrationActivity.this, "농장이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FarmRegistrationActivity.this, "농장 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (Exception e) {
                Log.e("RegisterFarm", "Error: " + e.getMessage());
                Toast.makeText(FarmRegistrationActivity.this, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Firebase Realtime Database에 농장 정보 등록 (이미지 없이)
            Farm farm = new Farm(farmEmail, farmName, farmDescription, "");
            databaseReference.child(farmId).setValue(farm);
            Toast.makeText(FarmRegistrationActivity.this, "농장이 등록되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getfarmEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail();
        }
        return null;
    }
}



