package com.example.Greenland;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private List<ChatItem> chatList;
    private OnItemClickListener onItemClickListener;
    private RecyclerView recyclerView;
    private boolean shouldScrollToBottom = true;

    public ChatListAdapter(List<ChatItem> chatList, RecyclerView recyclerView) {
        this.chatList = chatList;
        this.recyclerView = recyclerView;
        scrollToBottom();
    }

    public interface OnItemClickListener {
        void onItemClick(ChatItem chatItem);
        void onItemLongClick(ChatItem chatItem, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private void scrollToBottom() {
        int itemCount = chatList.size();
        if (itemCount > 0 && shouldScrollToBottom) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(itemCount - 1);
                }
            }, 100);
        }
    }


    public void setShouldScrollToBottom(boolean shouldScroll) {
        this.shouldScrollToBottom = shouldScroll;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem chatItem = chatList.get(position);
        holder.bind(chatItem, onItemClickListener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(chatItem);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemLongClick(chatItem, position);
                        return true;
                    }
                }
                return false;
            }
        });

        if (position == chatList.size() - 1) {
            scrollToBottom();
        }
    }

    public void removeChatItem(int position) {
        if (position >= 0 && position < chatList.size()) {
            chatList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timeTextView;
        TextView senderTextView;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
        }

        void bind(ChatItem chatItem, OnItemClickListener listener) {
            messageTextView.setText(chatItem.getMessage());
            timeTextView.setText(chatItem.getTime());

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserList");
            Query query = userRef.orderByChild("Email").equalTo(chatItem.getSenderId().replace("_", "."));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String nickname = userSnapshot.child("Nickname").getValue(String.class);
                            if (nickname != null) {
                                senderTextView.setText(nickname);
                            } else {
                                senderTextView.setText("Unknown");
                            }
                        }
                    } else {
                        senderTextView.setText("Unknown");
                        itemView.setOnClickListener(null);
                        itemView.setOnLongClickListener(null);
                        itemView.setClickable(false);
                        itemView.setFocusable(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}