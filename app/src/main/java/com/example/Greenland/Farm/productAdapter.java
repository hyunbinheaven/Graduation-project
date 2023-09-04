package com.example.Greenland.Farm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Greenland.DB.product;
import com.example.Greenland.R;
import com.example.Greenland.SellPage.SoldDetailPage;

import java.util.List;

public class productAdapter extends RecyclerView.Adapter<productAdapter.ViewHolder> {
    private Context mContext;
    private List<product> productList;

    public productAdapter(List<product> productList, Context context) {
        this.productList = productList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product product = productList.get(position);
        holder.bind(product);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 상세 제품 화면 페이지로 이동하는 코드 작성
                // 클릭된 제품의 정보를 상세 제품 화면으로 전달하여 표시할 수 있음
                Intent intent = new Intent(mContext, SoldDetailPage.class);
                intent.putExtra("productName", product.getproductname());
                intent.putExtra("productPrice", product.getproductprice());
                intent.putExtra("productContents", product.getproductcontents());
                intent.putExtra("imageUrl", product.getimageUrl());
                intent.putExtra("Email", product.getemail());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productNameTextView;
        private TextView productPriceTextView;
        private TextView productContentsTextView;
        private TextView productDatetimeTextView;
        private ImageView productImageUrlImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.name_text_view);
            productPriceTextView = itemView.findViewById(R.id.price_text_view);
            productContentsTextView = itemView.findViewById(R.id.contents_text_view);
            productDatetimeTextView = itemView.findViewById(R.id.current_date_time_text_view);
            productImageUrlImageView = itemView.findViewById(R.id.image_view);
        }

        public void bind(product product) {
            productNameTextView.setText(product.getproductname());
            productPriceTextView.setText(product.getproductprice());
            productContentsTextView.setText(product.getproductcontents());
            productDatetimeTextView.setText(product.getdatetime());

            Glide.with(itemView.getContext())
                    .load(product.getimageUrl())
                    .into(productImageUrlImageView);
        }
    }
}