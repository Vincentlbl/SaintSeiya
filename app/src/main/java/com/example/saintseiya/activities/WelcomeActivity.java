package com.example.saintseiya.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;


import com.example.saintseiya.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SeiyaPrefs";
    private static final String LAST_BLOCK_KEY = "lastBlockId";
    private MediaPlayer menuMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialiser et démarrer la musique du menu
        menuMusic = MediaPlayer.create(this, R.raw.menu_music);
        menuMusic.setLooping(true);
        menuMusic.start();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnNewGame = findViewById(R.id.btnNewGame);
        Button btnContinue = findViewById(R.id.btnContinue);

        btnNewGame.setOnClickListener(v -> {
            stopMenuMusic(); 
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        btnContinue.setOnClickListener(v -> {
            stopMenuMusic();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String lastBlockId = prefs.getString(LAST_BLOCK_KEY, "start");
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("blockId", lastBlockId);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.release();
            menuMusic = null;
        }
        super.onDestroy();
    }

    // Méthode pour arrêter la musique
    private void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.release();
            menuMusic = null;
        }
    }
}
