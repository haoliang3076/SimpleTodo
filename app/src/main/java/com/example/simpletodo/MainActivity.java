package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
//import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION="item_position";
    public static final int EDIT_TEXT_CODE = 20;
    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemAdpter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        ItemAdpter.OnLongClickListener onLongClickListener = new ItemAdpter.OnLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                // Delete the item from the model
                items.remove(position);

                //notify the adapter
                itemsAdapter.notifyItemMoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };

        ItemAdpter.OnClickListener onClickListener = new ItemAdpter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity","Single click at position"+ position);
                // create the new activity
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                // pass the data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };

        ItemAdpter itemAdpter = new ItemAdpter(items, onLongClickListener, onClickListener );
        rvItems.setAdapter(itemAdpter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                // add item to the model;
                items.add(todoItem);
                //Notify adapter that an item is inserted
                itemAdpter.notifyItemInserted(items.size() - 1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // handle the result of the edit acitity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            items.set(position, itemText);

            itemsAdapter.notifyItemChanged(position);

            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfuly!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unkown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // this function will load items by reading every line of the data file
    private void loadItems() {
        try {

            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);

        }
    }
}