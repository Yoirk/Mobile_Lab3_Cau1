package com.example.lab3bai2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper databaseHelper;
    ArrayAdapter<String> adapter;
    List<String> contactList;
    List<Integer> contactIds; // Lưu giữ danh sách ID để hỗ trợ xóa contact

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);

        // Thêm một số contact mẫu vào CSDL
        databaseHelper.addContact("Nguyễn Văn A", "123456789");
        databaseHelper.addContact("Trần Thị B", "987654321");

        // Lấy tất cả các contact và hiển thị lên ListView
        loadContacts();

        // Xử lý sự kiện long click để xóa contact
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Xóa contact khỏi CSDL
                int contactId = contactIds.get(position);
                databaseHelper.deleteContact(contactId);

                // Cập nhật lại danh sách contact và ListView
                loadContacts();
                Toast.makeText(MainActivity.this, "Đã xóa contact!", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hàm để load contact từ database và hiển thị lên ListView
    private void loadContacts() {
        contactList = new ArrayList<>();
        contactIds = new ArrayList<>();

        // Truy vấn CSDL để lấy tất cả contact
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                // Lấy thông tin contact
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE_NUMBER));

                // Thêm contact vào danh sách hiển thị
                contactList.add(name + " - " + phone);
                contactIds.add(id); // Lưu ID để sử dụng khi cần xóa
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // Cập nhật ListView với danh sách mới
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
        listView.setAdapter(adapter);
    }

}