package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

       loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener= new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
//                Delete the item from the model
                items.remove(position);
//                Notify the adapter that we deleted
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was Removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position" + position);
//              create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
//              pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
//              display the new activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };


        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvItems.getContext(),1);
        rvItems.addItemDecoration(dividerItemDecoration);


        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
//                Add item to the model if it is not blank
                if (!todoItem.equals(null) && !todoItem.isEmpty()){
                    items.add(todoItem);
//                Notify adapter that an item is inserted
                    itemsAdapter.notifyItemInserted(items.size()-1);
                    etItem.setText("");
                    Toast.makeText(getApplicationContext(), "Item was Added", Toast.LENGTH_SHORT).show();
                    saveItems();
                }
//                show a Toast saying that they should not leave the entry empty
                else {
                    Toast.makeText(getApplicationContext(), "Entry was empty! Please write something to add a new task!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//  handle the result of the edit activity
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
//          retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
//          extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
//          update the model at the position with the new item
            items.set(position, itemText);
//          notify the adapter
            itemsAdapter.notifyItemChanged(position);
//          persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

//    This function will load items by reading every line from data.txt file
    private void loadItems() {
        try {
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<String>();
        }
    }
//    This function saves items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
        }
        
}