package com.example.saintseiya.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saintseiya.R;
import com.example.saintseiya.model.Inventory;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    private ListView inventoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        inventoryList = findViewById(R.id.inventoryList);
        ArrayList<String> items = new ArrayList<>(Inventory.getInstance().getItems());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);

        inventoryList.setAdapter(adapter);

        inventoryList.setOnItemClickListener((parent, view, position, id) -> {
            String item = items.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Utiliser " + item + " ?")
                    .setMessage("Souhaitez-vous consommer cet objet maintenant ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        Inventory.getInstance().useItem(item);
                        Toast.makeText(this, item + " utilisé !", Toast.LENGTH_SHORT).show();
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });
    }
}
