package com.ddong.appfood_.Helper;

import android.content.Context;
import android.widget.Toast;


import com.ddong.appfood_.Domain.Foods;

import java.util.ArrayList;


public class ManagmentCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagmentCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }



    public void insertFood(Foods item) {
        ArrayList<Foods> listpop = getListCart();
        boolean existAlready = false;  //kiểm tra xem món ăn đã tồn tại trong giỏ hàng hay chưa
        int n = 0;

        //Duyệt qua từng món ăn trong danh sách giỏ hàng để kiểm tra xem món ăn cần thêm đã tồn tại hay chưa.
        for (int i = 0; i < listpop.size(); i++) {
            //  So sánh tiêu đề của món ăn trong giỏ hàng với tiêu đề của món ăn mới cần thêm.
            //  Nếu trùng khớp, đánh dấu rằng món ăn đã tồn tại và lưu lại vị trí của món ăn trong danh sách
            if (listpop.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = i;
                break;
            }
        }
        // Kiểm tra nếu món ăn đã tồn tại, thay đổi số lượng của nó trong giỏ hàng thành số lượng mới;
        // nếu không, thêm món ăn mới vào danh sách
        if (existAlready) {
            listpop.get(n).setNumberInCart(item.getNumberInCart());
        } else {
            listpop.add(item);
        }
        tinyDB.putListObject("CartList", listpop);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    //lấy danh sách các món ăn hiện có trong giỏ hàng
    public ArrayList<Foods> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    //tính tổng phí của tất cả các món ăn trong giỏ hàng
    public Double getTotalFee() {
        ArrayList<Foods> listItem = getListCart();
        double fee = 0;
        for (int i = 0; i < listItem.size(); i++) {
            fee = fee + (listItem.get(i).getPrice() * listItem.get(i).getNumberInCart());
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<Foods> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        //Kiểm tra nếu số lượng của món ăn đó là 1, tức là nếu giảm đi nữa sẽ là 0, thì loại bỏ món ăn đó khỏi giỏ hàng.
        if (listItem.get(position).getNumberInCart() == 1) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.change();
    }

    public void plusNumberItem(ArrayList<Foods> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.change();
    }
    public void updateCart(ArrayList<Foods> updatedOrderList) {
        // Xóa toàn bộ món ăn hiện có trong giỏ hàng
        // và thêm danh sách đơn hàng mới đã cập nhật
        clearCart();
        for (Foods food : updatedOrderList) {
            insertFood(food);
        }
    }
    private void clearCart() {
        // Xóa toàn bộ món ăn trong giỏ hàng
        tinyDB.remove("CartList");
    }


}
