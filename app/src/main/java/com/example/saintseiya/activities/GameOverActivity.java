package com.example.saintseiya.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saintseiya.R;

public class GameOverActivity extends AppCompatActivity {

    private TextView message;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        message = findViewById(R.id.gameOverMessage);
        retryButton = findViewById(R.id.retryButton);

        String cause = getIntent().getStringExtra("cause");
        String enemy = getIntent().getStringExtra("enemy");

        // Message personnalisé selon la cause de la mort
        if ("mort_sans_potion".equals(cause) && "Aldébaran".equals(enemy)) {
            message.setText("Aldébaran : \"Tu aurais dû réparer ton armure. C’est cela qui t’a coûté la vie...\"");
        } else if ("mort_fuite_taureau".equals(cause)) {
            message.setText("Aldébaran : \"On ne fuit pas un chevalier d’or...\"");
        } else {
            message.setText("Tu as échoué, mais l’espoir demeure...");
        }

        retryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("blockId", "start");
            startActivity(intent);
            finish();
        });
    }
}
