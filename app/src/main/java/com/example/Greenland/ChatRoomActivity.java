package com.example.Greenland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatRoomActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private List<ChatItem> chatList;
    private DatabaseReference chatDB;
    private ChatListAdapter chatListAdapter;

    private String receiveEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        RecyclerView recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList, recyclerView);
        recyclerView.setAdapter(chatListAdapter);

        chatDB = FirebaseDatabase.getInstance().getReference().child("Chats");
        Intent intent = getIntent();
        receiveEmail = intent.getStringExtra("sellerEmail");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserEmail = user.getEmail();

        chatDB.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUserEmailModified = currentUserEmail.replace(".", "_");
                chatList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.getKey(); // Get email
                    if (email.equals(currentUserEmailModified)) {
                        for (DataSnapshot messageSnapshot : userSnapshot.getChildren()) {
                            if (messageSnapshot.getKey().equals(receiveEmail)) {
                                for (DataSnapshot chatSnapshot : messageSnapshot.getChildren()) {
                                    String messageId = chatSnapshot.getKey();
                                    String date = chatSnapshot.child("date").getValue(String.class);
                                    String message = chatSnapshot.child("message").getValue(String.class);
                                    String senderEmail = chatSnapshot.child("sender").getValue(String.class);
                                    if (messageId != null && date != null && message != null) {
                                        ChatItem chatItem = new ChatItem();
                                        chatItem.setTime(date);
                                        chatItem.setMessage(message);
                                        chatItem.setSenderId(senderEmail);
                                        chatList.add(chatItem);
                                    }
                                }
                                break;
                            }
                        }
                        chatListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // 현재시간 넣기
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.KOREA);
        String nowTime = dateFormat.format(currentTime);



        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(view -> {
            EditText messageEditText = findViewById(R.id.messageEditText);
            String message = messageEditText.getText().toString();

            // 보낸 메시지를 채팅 목록에 추가하고 어댑터에 알림
            ChatItem sentChatItem = new ChatItem();
            sentChatItem.setMessage(message);
            sentChatItem.setSenderId(currentUserEmail);
            sentChatItem.setreceiveId(receiveEmail);
            sentChatItem.setTime(nowTime);
            chatList.add(sentChatItem);
            chatListAdapter.notifyDataSetChanged();
            DatabaseReference userRef = chatDB.child("User");

            // 현재 사용자의 메시지 데이터 설정
            String currentUserEmailModified = currentUserEmail.replace(".", "_");
            String receiveEmailModified = receiveEmail.replace(".", "_");


            // 현재 사용자의 메시지 데이터 설정
            DatabaseReference currentUserRef = userRef.child(currentUserEmailModified).child(receiveEmailModified);
            DatabaseReference receiveUserRef = userRef.child(receiveEmailModified).child(currentUserEmailModified);
            String messageId = currentUserRef.push().getKey();

            // 현재 사용자의 메시지 데이터 설정
            DatabaseReference currentMessageRef = currentUserRef.child(messageId);
            currentMessageRef.child("date").setValue(nowTime);
            currentMessageRef.child("message").setValue(message);
            currentMessageRef.child("sender").setValue(currentUserEmail);
            // 수신자의 메시지 데이터 설정
            DatabaseReference receiveMessageRef = receiveUserRef.child(messageId);
            receiveMessageRef.child("date").setValue(nowTime);
            receiveMessageRef.child("message").setValue(message);
            receiveMessageRef.child("sender").setValue(currentUserEmail);
            // 메시지 전송 성공 처리
            messageEditText.setText("");
        });
    }
}