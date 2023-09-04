package com.example.Greenland.BottomNavi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Greenland.Farm.FarmRegistrationActivity;
import com.example.Greenland.InfoEdit.EditUserActivity;
import com.example.Greenland.Login.LoginActivity;
import com.example.Greenland.R;
import com.example.Greenland.InfoEdit.EditPasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class myPageFragment extends Fragment {

    public myPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        TextView email_text = view.findViewById(R.id.emailTextView);
        TextView nick_text = view.findViewById(R.id.nicknameTextView);
        TextView address_text = view.findViewById(R.id.AddressTextView);
        TextView phone_text = view.findViewById(R.id.PhoneTextView);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("UserList");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        Query query = userRef.orderByChild("Email").equalTo(email);
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        Button farmRegist = view.findViewById(R.id.farm_button);
        farmRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FarmRegistrationActivity.class);
                startActivity(intent);
            }
        });
        Button editpassword = view.findViewById(R.id.edit_password);
        editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditPasswordActivity.class);
                startActivity(intent);
            }
        });
        Button edituser = view.findViewById(R.id.edit_user);
        edituser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditUserActivity.class);
                startActivity(intent);
            }
        });
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String nick = childSnapshot.child("Nickname").getValue(String.class);
                        String address = childSnapshot.child("Address").getValue(String.class);
                        String Phone = childSnapshot.child("PhoneNumber").getValue(String.class);
                        email_text.setText(email);
                        nick_text.setText(nick);
                        address_text.setText(address);
                        phone_text.setText(Phone);
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }
}