package com.example.Greenland;

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
import com.example.Greenland.SellPage.SoldDetailPage;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context mContext;
    private List<product> mProductList;

    public MyAdapter(Context context, List<product> productList) {
        mContext = context;
        mProductList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product product = mProductList.get(position);

        // Product 데이터를 ViewHolder의 View에 바인딩하는 코드
        holder.productNameTextView.setText(product.getproductname());
        holder.productPriceTextView.setText(product.getproductprice() + " 원");
        holder.productContentsTextView.setText(product.getnickname());

        // Glide 라이브러리를 사용해 이미지 로딩
        Glide.with(mContext).load(product.getimageUrl()).into(holder.productImageView);
        holder.currentDateTimeTextView.setText(product.getdatetime());

        // ViewHolder 클릭 이벤트 처리
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
        return mProductList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImageView;
        public TextView productNameTextView;
        public TextView productPriceTextView;
        public TextView productContentsTextView;
        public TextView currentDateTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.image_view);
            productNameTextView = itemView.findViewById(R.id.name_text_view);
            productPriceTextView = itemView.findViewById(R.id.price_text_view);
            productContentsTextView = itemView.findViewById(R.id.contents_text_view);
            currentDateTimeTextView = itemView.findViewById(R.id.current_date_time_text_view);
        }
    }
}