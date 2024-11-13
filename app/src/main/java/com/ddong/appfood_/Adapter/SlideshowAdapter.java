package com.ddong.appfood_.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddong.appfood_.R;

public class SlideshowAdapter extends RecyclerView.Adapter<SlideshowAdapter.ViewHolder> {
    private int[] imageResources = {R.drawable.slideshowone, R.drawable.slideshowtwo,
            R.drawable.slideshowthree,R.drawable.slideshowfore,R.drawable.slideshowfire};

    @NonNull
    @Override
    public SlideshowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.slideshow_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideshowAdapter.ViewHolder holder, int position) {
        int imageResource = imageResources[position];
        holder.slideshowItem.setImageResource(imageResource);
    }

    @Override
    public int getItemCount() {
        return imageResources.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView slideshowItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slideshowItem = itemView.findViewById(R.id.slideshowItem);
        }
    }
}
