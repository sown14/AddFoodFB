package com.ddong.appfood_.Adapter;

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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.ddong.appfood_.Activity.DetailActivity;
import com.ddong.appfood_.Domain.Item;
import com.ddong.appfood_.Domain.Order;
import com.ddong.appfood_.R;
import com.google.android.play.core.integrity.i;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class YourOrderAdapter extends RecyclerView.Adapter<YourOrderAdapter.ViewHolder> {
    private List<Item> itemList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",new Locale("vi", "VN"));


    public YourOrderAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public YourOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_your_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YourOrderAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvDishName.setText(item.getTitle());
        holder.tvQuantity.setText(" "+item.getNumberInCart());
        holder.tvTotalPrice.setText(String.format(Locale.getDefault(), "%,.2f VND", item.getPrice()));
        holder.tvtime.setText(" "+item.getTimeValue()+" minute");


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDishName, tvQuantity, tvTotalPrice,tvstatus,tvtime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDishName = itemView.findViewById(R.id.tv_hitory_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_hitory_product_num);
            tvTotalPrice = itemView.findViewById(R.id.tv_hitory_product_price);
            tvstatus=itemView.findViewById(R.id.tv_hitory_product_status);
            tvtime=itemView.findViewById(R.id.tv_hitory_product_time);

        }
    }
}
