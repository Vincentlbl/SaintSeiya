package com.example.saintseiya.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saintseiya.R;
import com.example.saintseiya.model.StoryBlock;
import com.example.saintseiya.utils.StoryLoader;

import java.util.ArrayList;
import java.util.List;

public class DebugActivity extends AppCompatActivity {

    private Spinner blockSpinner;
    private Button jumpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        blockSpinner = findViewById(R.id.spinnerBlocks);
        jumpButton = findViewById(R.id.btnJump);

        List<StoryBlock> blocks = StoryLoader.loadStory(this);
        List<String> blockIds = new ArrayList<>();
        for (StoryBlock block : blocks) {
            blockIds.add(block.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                blockIds
        );
        blockSpinner.setAdapter(adapter);

        jumpButton.setOnClickListener(v -> {
            String selectedBlock = blockSpinner.getSelectedItem().toString();
            Intent intent = new Intent(DebugActivity.this, MainActivity.class);
            intent.putExtra("blockId", selectedBlock);
            startActivity(intent);
            finish();
        });
    }
}
