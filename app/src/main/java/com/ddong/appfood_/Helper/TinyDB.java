/*
 * Copyright 2014 KC Ochibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  The "‚‗‚" character is not a comma, it is the SINGLE LOW-9 QUOTATION MARK unicode 201A
 *  and unicode 2017 that are used for separating the items in a list.
 */

package com.ddong.appfood_.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;


import com.ddong.appfood_.Domain.Foods;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class TinyDB {

    private SharedPreferences preferences;
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";


    //nhận một đối tượng Context làm tham số. Context là một đối tượng cung cấp thông tin về môi trường của ứng dụng.
    public TinyDB(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }


    //Đây là phương thức để lấy một đối tượng Bitmap từ một đường dẫn file ảnh. Nó nhận một chuỗi path là đường dẫn
    // tới file ảnh và trả về một đối tượng Bitmap tương ứng.
    // Phương thức này sử dụng lớp BitmapFactory để giải mã file ảnh và tạo đối tượng Bitmap.
    public Bitmap getImage(String path) {
        Bitmap bitmapFromPath = null;
        try {
            bitmapFromPath = BitmapFactory.decodeFile(path);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return bitmapFromPath;
    }


    public String getSavedImagePath() {
        return lastImagePath;
    }


    // Trước khi lưu trữ hình ảnh, phương thức này kiểm tra xem các đối số truyền vào có null không.
    // Nếu không, nó gán giá trị của theFolder vào biến DEFAULT_APP_IMAGEDATA_DIRECTORY
    //sau đó nó gọi phương thức setupFullPath để tạo đường dẫn đầy đủ của hình ảnh.
    // Nếu đường dẫn không rỗng, nó lưu đường dẫn này vào biến lastImagePath
    // và gọi phương thức saveBitmap để lưu trữ hình ảnh vào đường dẫn đã cho.
    // Cuối cùng, phương thức trả về đường dẫn đầy đủ của hình ảnh
    public String putImage(String theFolder, String theImageName, Bitmap theBitmap) {
        if (theFolder == null || theImageName == null || theBitmap == null)
            return null;

        this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
        String mFullPath = setupFullPath(theImageName);

        if (!mFullPath.equals("")) {
            lastImagePath = mFullPath;
            saveBitmap(mFullPath, theBitmap);
        }

        return mFullPath;
    }


    // kiểm tra xem fullPath và theBitmap có null không. Nếu không, nó gọi phương thức saveBitmap để lưu trữ hình ảnh vào đường dẫn đã cho.
    public boolean putImageWithFullPath(String fullPath, Bitmap theBitmap) {
        return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap);
    }


    // để tạo đường dẫn hoàn chỉnh cho một tệp ảnh dựa trên tên tệp và thư mục mặc định.
    //tạo một đối tượng File để đại diện cho thư mục mà tệp ảnh sẽ được lưu trữ.
    // Thư mục này được tạo trong bộ nhớ ngoài của thiết bị, và tên thư mục được chỉ định bởi DEFAULT_APP_IMAGEDATA_DIRECTORY.
    //Sau đó, phương thức kiểm tra xem bộ nhớ ngoài có thể đọc và ghi được không thông qua hàm isExternalStorageReadable() và isExternalStorageWritable().
    // Nếu điều kiện này được đáp ứng và thư mục không tồn tại (!mFolder.exists()), thì phương thức sẽ tiến hành tạo thư mục.
    private String setupFullPath(String imageName) {
        File mFolder = new File(Environment.getExternalStorageDirectory(), DEFAULT_APP_IMAGEDATA_DIRECTORY);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists()) {
            if (!mFolder.mkdirs()) {
                Log.e("ERROR", "Failed to setup folder");
                return "";
            }
        }

        return mFolder.getPath() + '/' + imageName;
    }

    private boolean saveBitmap(String fullPath, Bitmap bitmap) {
        if (fullPath == null || bitmap == null)
            return false;

        boolean fileCreated = false;
        boolean bitmapCompressed = false;
        boolean streamClosed = false;

        File imageFile = new File(fullPath);

        if (imageFile.exists())
            if (!imageFile.delete())
                return false;

        try {
            fileCreated = imageFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            bitmapCompressed = bitmap.compress(CompressFormat.PNG, 100, out);

        } catch (Exception e) {
            e.printStackTrace();
            bitmapCompressed = false;

        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    streamClosed = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    streamClosed = false;
                }
            }
        }

        return (fileCreated && bitmapCompressed && streamClosed);
    }


    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }


    public ArrayList<Integer> getListInt(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Integer> newList = new ArrayList<Integer>();

        for (String item : arrayToList)
            newList.add(Integer.parseInt(item));

        return newList;
    }


    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }


    public float getFloat(String key) {
        return preferences.getFloat(key, 0);
    }


    public double getDouble(String key) {
        String number = getString(key);

        try {
            return Double.parseDouble(number);

        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public ArrayList<Double> getListDouble(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Double> newList = new ArrayList<Double>();

        for (String item : arrayToList)
            newList.add(Double.parseDouble(item));

        return newList;
    }

    public ArrayList<Long> getListLong(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Long> newList = new ArrayList<Long>();

        for (String item : arrayToList)
            newList.add(Long.parseLong(item));

        return newList;
    }


    public String getString(String key) {
        return preferences.getString(key, "");
    }


    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }


    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }


    public ArrayList<Boolean> getListBoolean(String key) {
        ArrayList<String> myList = getListString(key);
        ArrayList<Boolean> newList = new ArrayList<Boolean>();

        for (String item : myList) {
            if (item.equals("true")) {
                newList.add(true);
            } else {
                newList.add(false);
            }
        }

        return newList;
    }


    public ArrayList<Foods> getListObject(String key) {
        Gson gson = new Gson();

        ArrayList<String> objStrings = getListString(key);
        ArrayList<Foods> playerList = new ArrayList<Foods>();

        for (String jObjString : objStrings) {
            Foods player = gson.fromJson(jObjString, Foods.class);
            playerList.add(player);
        }
        return playerList;
    }


    public <T> T getObject(String key, Class<T> classOfT) {

        String json = getString(key);
        Object value = new Gson().fromJson(json, classOfT);
        if (value == null)
            throw new NullPointerException();
        return (T) value;
    }


    // Put methods


    public void putInt(String key, int value) {
        checkForNullKey(key);
        preferences.edit().putInt(key, value).apply();
    }


    public void putListInt(String key, ArrayList<Integer> intList) {
        checkForNullKey(key);
        Integer[] myIntList = intList.toArray(new Integer[intList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply();
    }


    public void putLong(String key, long value) {
        checkForNullKey(key);
        preferences.edit().putLong(key, value).apply();
    }


    public void putListLong(String key, ArrayList<Long> longList) {
        checkForNullKey(key);
        Long[] myLongList = longList.toArray(new Long[longList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myLongList)).apply();
    }


    public void putFloat(String key, float value) {
        checkForNullKey(key);
        preferences.edit().putFloat(key, value).apply();
    }


    public void putDouble(String key, double value) {
        checkForNullKey(key);
        putString(key, String.valueOf(value));
    }

    public void putListDouble(String key, ArrayList<Double> doubleList) {
        checkForNullKey(key);
        Double[] myDoubleList = doubleList.toArray(new Double[doubleList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myDoubleList)).apply();
    }

    public void putString(String key, String value) {
        checkForNullKey(key);
        checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }


    public void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public void putBoolean(String key, boolean value) {
        checkForNullKey(key);
        preferences.edit().putBoolean(key, value).apply();
    }


    public void putListBoolean(String key, ArrayList<Boolean> boolList) {
        checkForNullKey(key);
        ArrayList<String> newList = new ArrayList<String>();

        for (Boolean item : boolList) {
            if (item) {
                newList.add("true");
            } else {
                newList.add("false");
            }
        }

        putListString(key, newList);
    }


    public void putObject(String key, Object obj) {
        checkForNullKey(key);
        Gson gson = new Gson();
        putString(key, gson.toJson(obj));
    }

    public void putListObject(String key, ArrayList<Foods> playerList) {
        checkForNullKey(key);
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (Foods player : playerList) {
            objStrings.add(gson.toJson(player));
        }
        putListString(key, objStrings);
    }


    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }


    public boolean deleteImage(String path) {
        return new File(path).delete();
    }


    public void clear() {
        preferences.edit().clear().apply();
    }


    public Map<String, ?> getAll() {
        return preferences.getAll();
    }


    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        preferences.registerOnSharedPreferenceChangeListener(listener);
    }


    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }


    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private void checkForNullKey(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
    }

    private void checkForNullValue(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }

    //chuyển đổi ảnh fav
    public void putFavoriteStatus(String key, boolean isFavorite) {
        checkForNullKey(key);
        preferences.edit().putBoolean(key, isFavorite).apply();
    }
    public boolean getFavoriteStatus(String key) {
        return preferences.getBoolean(key, false); // Giá trị mặc định là false nếu key chưa tồn tại
    }
}

//Lưu trữ và truy xuất dữ liệu

//Dữ liệu nguyên thủy (primitive)
//putInt(String key, int value): Lưu trữ một giá trị integer.
//getInt(String key): Truy xuất giá trị integer dựa trên khóa.
//Tương tự cho putLong(), getLong(), putFloat(), getFloat(), putBoolean(), getBoolean().

//Dữ liệu kiểu chuỗi (String)
//putString(String key, String value): Lưu trữ một chuỗi.
//getString(String key): Truy xuất một chuỗi dựa trên khóa.

//Dữ liệu kiểu đối tượng (Object)
//putObject(String key, Object obj): Lưu trữ một đối tượng. Đối tượng này sẽ được chuyển đổi thành chuỗi JSON trước khi lưu trữ.
//getObject(String key, Class<T> classOfT): Truy xuất một đối tượng dựa trên khóa và lớp của đối tượng.

//Dữ liệu danh sách
//Có các phương thức tương tự cho danh sách các kiểu dữ liệu, ví dụ: putListInt(), getListInt(), putListString(), getListString() và như vậy.

//Dữ liệu hình ảnh
//putImage(String theFolder, String theImageName, Bitmap theBitmap): Lưu trữ một hình ảnh với đường dẫn và tên tệp.
//getImage(String path): Truy xuất một hình ảnh từ đường dẫn đã cung cấp.

//Xóa dữ liệu
//remove(String key): Xóa một cặp key-value từ SharedPreferences.
//clear(): Xóa tất cả dữ liệu trong SharedPreferences.

//Ghi nhận sự kiện thay đổi
//registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener): Đăng ký một lắng nghe sự kiện thay đổi SharedPreferences.
//unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener): Hủy đăng ký một lắng nghe sự kiện thay đổi SharedPreferences.

//Kiểm tra trạng thái bộ nhớ ngoài
//isExternalStorageWritable(): Kiểm tra xem bộ nhớ ngoài có thể ghi được hay không.
//isExternalStorageReadable(): Kiểm tra xem bộ nhớ ngoài có thể đọc được hay không.