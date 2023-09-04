package com.example.Greenland.Farm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Greenland.DB.Farm;
import com.example.Greenland.R;

import java.util.List;

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.FarmViewHolder> {

    private List<Farm> farmList;
    private OnFarmClickListener onFarmClickListener;

    public FarmAdapter(List<Farm> farmList, OnFarmClickListener onFarmClickListener) {
        this.farmList = farmList;
        this.onFarmClickListener = onFarmClickListener;
    }

    @NonNull
    @Override
    public FarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farm, parent, false);
        return new FarmViewHolder(view, onFarmClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmViewHolder holder, int position) {
        Farm farm = farmList.get(position);
        holder.bind(farm);
    }

    @Override
    public int getItemCount() {
        return farmList.size();
    }

    public interface OnFarmClickListener {
        void onFarmClick(int position);
    }

    static class FarmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView farmNameTextView;
        private TextView farmDescriptionTextView;
        private ImageView farmImageView; // Add ImageView
        private OnFarmClickListener onFarmClickListener;

        FarmViewHolder(@NonNull View itemView, OnFarmClickListener onFarmClickListener) {
            super(itemView);
            farmNameTextView = itemView.findViewById(R.id.farm_name_text_view);
            farmDescriptionTextView = itemView.findViewById(R.id.farm_description_text_view);
            farmImageView = itemView.findViewById(R.id.farm_image_view); // Initialize ImageView
            this.onFarmClickListener = onFarmClickListener;

            itemView.setOnClickListener(this);
        }

        void bind(Farm farm) {
            farmNameTextView.setText(farm.getfarmName());
            farmDescriptionTextView.setText(farm.getfarmDescription());


            String profileImageUrl = farm.getfarmImageUrl();

            Glide.with(itemView.getContext())
                    .load(profileImageUrl)
                    .placeholder(R.drawable.carrot) // Optional placeholder image while loading
                    .into(farmImageView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onFarmClickListener.onFarmClick(position);
            }
        }
    }
}