package com.example.Greenland.BottomNavi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Greenland.DB.Farm;
import com.example.Greenland.Farm.FarmAdapter;
import com.example.Greenland.Farm.FarmDetailActivity;
import com.example.Greenland.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FarmFragment extends Fragment implements FarmAdapter.OnFarmClickListener {

    private RecyclerView recyclerView;
    private FarmAdapter farmAdapter;
    private List<Farm> farmList;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farm, container, false);

        recyclerView = view.findViewById(R.id.farm_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        farmList = new ArrayList<>();
        farmAdapter = new FarmAdapter(farmList, this);
        recyclerView.setAdapter(farmAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("farms");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                farmList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Farm farm = snapshot.getValue(Farm.class);
                    if (farm != null) {
                        farmList.add(farm);
                    }
                }
                farmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "데이터를 불러오는 중에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onFarmClick(int position) {
        Farm selectedFarm = farmList.get(position);

        Intent intent = new Intent(getContext(), FarmDetailActivity.class);
        intent.putExtra("farmEmail", selectedFarm.getfarmEmail());
        intent.putExtra("farmName", selectedFarm.getfarmName());
        intent.putExtra("farmDescription", selectedFarm.getfarmDescription());
        intent.putExtra("farmImageUrl", selectedFarm.getfarmImageUrl());
        startActivity(intent);
    }
}