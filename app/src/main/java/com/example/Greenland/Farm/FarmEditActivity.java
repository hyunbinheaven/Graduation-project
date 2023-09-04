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

import com.bumptech.glide.Glide;
import com.example.Greenland.DB.Farm;
import com.example.Greenland.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class FarmEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText farmNameEditText;
    private EditText farmDescriptionEditText;
    private Button updateButton;
    private Button deleteButton;
    private Button selectImageButton;
    private ImageView farmImageView;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri imageUri;

    private String farmEmail;
    private Farm farm;
    private String farmPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_edit);

        databaseReference = FirebaseDatabase.getInstance().getReference("farms");
        storageReference = FirebaseStorage.getInstance().getReference("farm_images");

        farmNameEditText = findViewById(R.id.farm_name_edit_text);
        farmDescriptionEditText = findViewById(R.id.farm_description_edit_text);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);
        selectImageButton = findViewById(R.id.select_image_button);
        farmImageView = findViewById(R.id.farm_image_view);

        Intent intent = getIntent();
        farmEmail = intent.getStringExtra("farmEmail");

        if (farmEmail == null) {
            Toast.makeText(this, "농장 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            farmPath = farmEmail.replace(".", "_");
        }

        // 농장 정보를 가져와서 초기화
        DatabaseReference farmRef = databaseReference.child(farmPath);
        farmRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                farm = dataSnapshot.getValue(Farm.class);
                if (farm != null) {
                    farmNameEditText.setText(farm.getfarmName());
                    farmDescriptionEditText.setText(farm.getfarmDescription());

                    // 이미지 로드
                    if (!farm.getfarmImageUrl().isEmpty()) {
                        Glide.with(FarmEditActivity.this)
                                .load(farm.getfarmImageUrl())
                                .into(farmImageView);
                    }
                } else {
                    Toast.makeText(FarmEditActivity.this, "농장 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String farmName = farmNameEditText.getText().toString().trim();
                String farmDescription = farmDescriptionEditText.getText().toString().trim();

                if (farmName.isEmpty()) {
                    farmNameEditText.setError("농장 이름을 입력해주세요.");
                    farmNameEditText.requestFocus();
                    return;
                }

                if (farmDescription.isEmpty()) {
                    farmDescriptionEditText.setError("농장 설명을 입력해주세요.");
                    farmDescriptionEditText.requestFocus();
                    return;
                }

                updateFarm(farmName, farmDescription);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFarm();
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
            Glide.with(this).load(imageUri).into(farmImageView);
        }
    }

    private void updateFarm(String farmName, String farmDescription) {
        if (imageUri != null) {
            // 이미지 업로드
            StorageReference imageRef = storageReference.child(UUID.randomUUID().toString());
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            updateFarmData(farmName, farmDescription, imageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FarmEditActivity.this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateFarmData(farmName, farmDescription, farm.getfarmImageUrl());
        }
    }

    private void updateFarmData(String farmName, String farmDescription, String imageUrl) {
        DatabaseReference farmRef = databaseReference.child(farmPath);
        Farm updatedFarm = new Farm(farm.getfarmEmail(), farmName, farmDescription, imageUrl);
        farmRef.setValue(updatedFarm).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FarmEditActivity.this, "농장 정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FarmEditActivity.this, "농장 정보 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFarm() {
        DatabaseReference farmRef = databaseReference.child(farmPath);
        farmRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FarmEditActivity.this, "농장 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FarmEditActivity.this, "농장 정보 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}