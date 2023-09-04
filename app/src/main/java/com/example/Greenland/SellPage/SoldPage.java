package com.example.Greenland.SellPage;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.Greenland.BottomNavi.HomeFragment;
import com.example.Greenland.HomeActivity;
import com.example.Greenland.R;
import com.example.Greenland.DB.product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.tensorflow.lite.Interpreter;

import java.util.Map;
import java.util.UUID;


public class SoldPage extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static ImageView imageView;
    Button sellbtn;
    EditText title, price, contents;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("UserList");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String email = currentUser.getEmail();
    String currentDateTimeString;
    String nickname;
    private String selectedCategory;
    Query query = userRef.orderByChild("Email").equalTo(email);

    private static final String TAG = "SoldActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold);


        // Find the ImageView in the activity_sold layout
        imageView = findViewById(R.id.photo_view1);
        sellbtn = findViewById(R.id.sell_btn);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        contents = findViewById(R.id.contents);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        currentDateTimeString = dateFormat.format(currentDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        nickname = childSnapshot.child("Nickname").getValue(String.class);
                    }
                } else {
                    Log.d("SoldPage", "Data snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
                Log.d("SoldPage", "Database error: " + databaseError.getMessage());
            }
        });


        sellbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sellbtn.setClickable(false);
                String name = ((EditText) findViewById(R.id.title)).getText().toString();
                String price = ((EditText) findViewById(R.id.price)).getText().toString();
                String contents = ((EditText) findViewById(R.id.contents)).getText().toString();

                if(name.isEmpty() || price.isEmpty() || contents.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    sellbtn.setClickable(true);
                    return;
                }

                Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                uploadImage(imageBitmap);
                Intent intent = new Intent(SoldPage.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        // Retrieve the Bitmap passed from ExampleActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bitmap imageBitmap = extras.getParcelable("image_bitmap");
            setImageView(imageBitmap);
        }
        price.addTextChangedListener(new PriceTextWatcher(price));

        if (getIntent().hasExtra("image_bitmap")) {
            Bitmap imageBitmap = getIntent().getParcelableExtra("image_bitmap");
            imageView.setImageBitmap(imageBitmap);
        } else if (getIntent().hasExtra("image_uri")) {
            Uri imageUri = Uri.parse(getIntent().getStringExtra("image_uri"));
            imageView.setImageURI(imageUri);
        }

        Button analyzeBtn = findViewById(R.id.analyze_btn);
        analyzeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyImage();
            }
        });
    }
    private void uploadImage(Bitmap bitmap) {
        // Firebase Storage 인스턴스 가져오기
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // StorageReference 만들기
        StorageReference storageRef = storage.getReference();
        // 업로드할 파일명 생성
        String filename = System.currentTimeMillis() + ".jpg";
        // 업로드할 경로 설정
        StorageReference imageRef = storageRef.child("images/" + filename);

        int targetWidth = 800;
        int targetHeight = 800;

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
        // Bitmap을 byte 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // 파일 업로드
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // 업로드 성공
                sellbtn.setClickable(true);

                // 업로드한 이미지의 다운로드 URL 가져오기
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // 다운로드 URL을 이용하여 이미지를 보여줄 수 있음
                        String imageUrl = uri.toString();
                        Log.d(TAG, "imageUrl: " + imageUrl);

                        // TODO: 업로드한 이미지 URL을 Firebase Realtime Database에 저장

                        addproduct(title.getText().toString(), price.getText().toString(), contents.getText().toString(), imageUrl, email, currentDateTimeString, nickname);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 업로드 실패
                Toast.makeText(SoldPage.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "uploadImage", exception);
                sellbtn.setClickable(true);
            }
        });
    }
    public void addproduct(String productname, String productprice, String productcontents, String imageUrl,String email, String currentDateTimeString, String nickname) {
        product product = new product(productname, productprice, productcontents, imageUrl,email, currentDateTimeString, nickname);
        databaseReference.child("product").push().setValue(product);
    }
    public static void setImageView(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private class PriceTextWatcher implements TextWatcher {
        private final EditText editText;

        PriceTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            editText.removeTextChangedListener(this); // Prevent recursive call

            String input = s.toString();
            String formattedInput = formatPrice(input);
            editText.setText(formattedInput);
            editText.setSelection(formattedInput.length());

            editText.addTextChangedListener(this);
        }

        private String formatPrice(String price) {
            String cleanPrice = price.replaceAll("[^\\d]", ""); // Remove non-digit characters
            StringBuilder formattedPrice = new StringBuilder(cleanPrice);

            // Add commas to the price
            for (int i = formattedPrice.length() - 3; i > 0; i -= 3) {
                formattedPrice.insert(i, ",");
            }

            return formattedPrice.toString();
        }
    }
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(SoldPage.this, modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappedByteBuffer loadModelFile(Activity activity, String modelPath) {
        AssetFileDescriptor fileDescriptor = null;
        FileInputStream inputStream = null;
        FileChannel fileChannel = null;
        MappedByteBuffer modelBuffer = null;

        try {
            fileDescriptor = activity.getAssets().openFd(modelPath);
            inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileDescriptor != null)
                    fileDescriptor.close();
                if (inputStream != null)
                    inputStream.close();
                if (fileChannel != null)
                    fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return modelBuffer;
    }
    private ByteBuffer preprocessImage(Bitmap bitmap) {
        int inputWidth = 150;
        int inputHeight = 150;
        int numChannels = 3;

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true);

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(inputWidth * inputHeight * numChannels * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[inputWidth * inputHeight];
        resizedBitmap.getPixels(pixels, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        for (int i = 0; i < inputWidth; i++) {
            for (int j = 0; j < inputHeight; j++) {
                int pixelValue = pixels[i * inputWidth + j];

                // Extract the RGB values from the pixel value
                float r = ((pixelValue >> 16) & 0xFF) / 255.0f;
                float g = ((pixelValue >> 8) & 0xFF) / 255.0f;
                float b = (pixelValue & 0xFF) / 255.0f;

                // Normalize the pixel values and add them to the input buffer
                inputBuffer.putFloat(r);
                inputBuffer.putFloat(g);
                inputBuffer.putFloat(b);
            }
        }
        inputBuffer.rewind();

        return inputBuffer;
    }

    private void classifyImage() {
        float[][] output = new float[1][8];
        int maxIndex = 0;

        try {
            Bitmap originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Interpreter lite = getTfliteInterpreter("vegetable_model.tflite");

            ByteBuffer input = preprocessImage(bitmap);

            lite.run(input, output);

            TextView tv_output = findViewById(R.id.Result);

            float maxProb = output[0][0];
            for (int i = 0; i < 8; i++) {
                if (output[0][i] > maxProb) {
                    maxIndex = i;
                    maxProb = output[0][i];
                }
            }
            Map<Integer, String> labelMap = new HashMap<>();
            labelMap.put(0, "브로콜리");
            labelMap.put(1, "양배추");
            labelMap.put(2, "파프리카");
            labelMap.put(3, "당근");
            labelMap.put(4, "감자");
            labelMap.put(5, "호박");
            labelMap.put(6, "무");
            labelMap.put(7, "토마토");
            String predictedLabel = labelMap.get(maxIndex);
            String classificationResult = predictedLabel + "로 분류됩니다.";
            tv_output.setText(classificationResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}