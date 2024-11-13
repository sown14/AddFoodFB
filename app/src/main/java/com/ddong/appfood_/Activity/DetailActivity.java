package com.ddong.appfood_.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.ddong.appfood_.Domain.Foods;
import com.ddong.appfood_.Helper.ManagmentCart;
import com.ddong.appfood_.Helper.TinyDB;
import com.ddong.appfood_.R;
import com.ddong.appfood_.databinding.ActivityDetailBinding;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    private ManagmentCart managmentCart;
    private TinyDB tinyDB;
    private static final String FAVORITE_KEY = "favorite_status";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tinyDB=new TinyDB(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        updateFavoriteIcon(tinyDB.getFavoriteStatus(FAVORITE_KEY));
        managmentCart = new ManagmentCart(this);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);
        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar() + "Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText(num * object.getPrice() + "$");
        binding.timeTxT.setText(object.getTimeValue() + "min");

        binding.plusBtn.setOnClickListener(v -> {
            num = num + 1;
            binding.numTxt.setText(num + "");
            binding.totalTxt.setText("$" + (num * object.getPrice()));
        });
        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num = num - 1;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);

        });
        binding.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavorite = !tinyDB.getFavoriteStatus(FAVORITE_KEY);
                tinyDB.putFavoriteStatus(FAVORITE_KEY, isFavorite);
                updateFavoriteIcon(isFavorite);
            }
        });
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            binding.favBtn.setImageResource(R.drawable.favorite_red);
            // Thêm tên sản phẩm vào danh sách yêu thích
            ArrayList<String> favorites = tinyDB.getListString("favorite_products");
            if (!favorites.contains(object.getTitle())) {
                favorites.add(object.getTitle());
                tinyDB.putListString("favorite_products", favorites);
            }
        } else {
            binding.favBtn.setImageResource(R.drawable.favorite_white);
            // Xóa tên sản phẩm khỏi danh sách yêu thích
            ArrayList<String> favorites = tinyDB.getListString("favorite_products");
            favorites.remove(object.getTitle());
            tinyDB.putListString("favorite_products", favorites);
        }
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}