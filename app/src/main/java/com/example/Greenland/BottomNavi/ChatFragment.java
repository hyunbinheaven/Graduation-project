package com.example.Greenland.BottomNavi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Greenland.ChatItem;
import com.example.Greenland.ChatListAdapter;
import com.example.Greenland.ChatRoomActivity;
import com.example.Greenland.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference chatDB;
    private ChatListAdapter chatListAdapter;
    private List<ChatItem> chatList;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        chatDB = FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .child("User")
                .child(currentUser.getEmail().replace(".", "_")); // 이메일을 사용하여 경로 설정
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chatlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatList, recyclerView);
        recyclerView.setAdapter(chatListAdapter);

        // Click event handling
        chatListAdapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ChatItem chatItem) {
                // Code to handle the clicked item
                String Email = chatItem.getSenderId();
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("sellerEmail", Email);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(ChatItem chatItem, int position) {
                // Code to handle the long clicked item (delete chat room)
                String email = chatItem.getSenderId();
                deleteChatRoom(email, position);
            }
        });

        return view;
    }

    private void deleteChatRoom(String email, int position) {
        DatabaseReference chatRoomDB = FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .child("User")
                .child(auth.getCurrentUser().getEmail().replace(".", "_"))
                .child(email.replace(".", "_"));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("채팅방 나가기");
        builder.setMessage("채팅방에서 나가시겠습니까?");
        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove chat room from Firebase Realtime Database
                chatRoomDB.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Remove chat item from the list and update UI
                                chatListAdapter.removeChatItem(position);
                                Toast.makeText(getActivity(), "삭제가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // onResume 메서드에서 채팅 목록을 다시 가져옵니다.
        loadChatList();
    }

    // 채팅 목록을 가져오는 메서드
    private void loadChatList() {
        chatList.clear(); // 기존의 채팅 목록을 초기화합니다.
        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String email = snapshot.getKey(); // 이메일 가져오기
                ChatItem lastChatItem = null;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String message = userSnapshot.child("message").getValue(String.class);
                    String date = userSnapshot.child("date").getValue(String.class);
                    if (message != null) {
                        lastChatItem = new ChatItem(date, message, email, "", "");
                    }
                }
                if (lastChatItem != null) {
                    chatList.add(lastChatItem);
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}