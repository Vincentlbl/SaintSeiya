package com.example.saintseiya.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.saintseiya.R;
import com.example.saintseiya.model.Choice;
import com.example.saintseiya.model.Inventory;
import com.example.saintseiya.model.StoryBlock;
import com.example.saintseiya.utils.StoryLoader;
import android.media.MediaPlayer;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textStory;
    private LinearLayout choiceContainer;
    private HashMap<String, StoryBlock> storyMap;

    private static final String PREFS_NAME = "SeiyaPrefs";
    private static final String LAST_BLOCK_KEY = "lastBlockId";
    private static final String ARMOR_REPAIRED_KEY = "armorRepaired";

    private MediaPlayer mediaPlayer;

    private String currentBlockId = "start";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        textStory = findViewById(R.id.textStory);
        choiceContainer = findViewById(R.id.choiceContainer);

        Button debugButton = findViewById(R.id.debugButton);
        debugButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DebugActivity.class);
            startActivity(intent);
        });

        List<StoryBlock> story = StoryLoader.loadStory(this);

        if (story == null || story.isEmpty()) {
            textStory.setText("Erreur : Impossible de charger le scénario.");
            Log.e("MainActivity", "Chargement du scénario impossible ou vide.");
            return;
        }

        storyMap = new HashMap<>();
        for (StoryBlock block : story) {
            storyMap.put(block.getId(), block);
        }

        String startBlockId = getIntent().getStringExtra("blockId");
        if (startBlockId == null) startBlockId = "start";

        showStoryBlock(startBlockId);
    }

    private void showStoryBlock(String blockId) {
        currentBlockId = blockId;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LAST_BLOCK_KEY, blockId).apply();

        StoryBlock currentBlock = storyMap.get(blockId);
        if (currentBlock == null) {
            textStory.setText("Erreur : Bloc narratif introuvable !");
            Log.e("MainActivity", "Bloc introuvable : " + blockId);
            return;
        }

        textStory.setTextColor(Color.WHITE);
        textStory.setTextSize(18f);
        textStory.setTypeface(Typeface.DEFAULT);
        choiceContainer.removeAllViews();

        if (currentBlock.getGiveItem() != null) {
            Inventory.getInstance().addItem(currentBlock.getGiveItem());
        }

        if ("reparation_belier".equals(blockId)) {
            editor.putBoolean(ARMOR_REPAIRED_KEY, true).apply();
        }

        if (currentBlock.isGameOver()) {
            boolean repaired = prefs.getBoolean(ARMOR_REPAIRED_KEY, false);
            textStory.setText("");

            switch (blockId) {
                case "mort_sans_potion":
                    textStory.setText(repaired
                            ? "Tu as été vaincu malgré la réparation... Il faudra plus qu’une armure pour vaincre les chevaliers d’or."
                            : "Aldébaran : \"Tu aurais dû réparer ton armure… Ça t’a coûté la vie, chevalier.\"");
                    break;
                case "mort_fuite_taureau":
                    textStory.setText("Aldébaran : \"Fuir ?! Tu n’es pas digne de porter cette armure !\"");
                    break;
                case "mort_illusion":
                    textStory.setText("Saga : \"Piégé par une illusion... Tu étais trop faible d’esprit, Seiya.\"");
                    break;
                default:
                    textStory.setText(currentBlock.getText());
                    break;
            }

            textStory.postDelayed(() -> {
                Inventory.getInstance().clearInventory();
                prefs.edit().clear().apply();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("blockId", "start");
                startActivity(intent);
                finish();
            }, 4000);

            return;
        }

        textStory.setText(currentBlock.getText());

        for (Choice choice : currentBlock.getChoices()) {
            if (choice.getRequiredItem() != null && !Inventory.getInstance().hasItem(choice.getRequiredItem())) {
                continue;
            }

            Button button = new Button(this);
            button.setText(choice.getText());
            button.setTextColor(Color.WHITE);
            button.setTextSize(18f);
            button.setAllCaps(false);
            button.setPadding(32, 24, 32, 24);
            button.setBackgroundResource(R.drawable.button_saint);

            button.setOnClickListener(v -> {
                String next = choice.getNextBlockId();

                switch (next) {
                    case "combat_aldebaran":
                        launchCombat("Aldébaran", "aldebaran", prefs.getBoolean(ARMOR_REPAIRED_KEY, false));
                        break;
                    case "combat_saga":
                        launchCombat("Saga", "saga", true);
                        break;
                    case "combat_deathmask":
                        launchCombat("Deathmask", "deathmask", true);
                        break;
                    case "combat_aiolia":
                        launchCombat("Aiolia", "aiolia", true);
                        break;
                    case "combat_shaka":
                        launchCombat("Shaka", "shaka", true);
                        break;
                    case "combat_milo":
                        launchCombat("Milo", "milo", true);
                        break;
                    case "combat_aiolos":
                        launchCombat("Aiolos", "aiolos", true);
                        break;
                    case "combat_shura":
                        launchCombat("Shura", "shura", true);
                        break;
                    case "combat_camus":
                        launchCombat("Camus", "camus", true);
                        break;
                    case "combat_aphrodite":
                        launchCombat("Aphrodite", "aphrodite", true);
                        break;
                    case "combat_grand_pope":
                        launchCombat("Grand Pope", "grand_pope", true);
                        break;
                    default:
                        showStoryBlock(next);
                        break;
                }
            });

            choiceContainer.addView(button);
        }
    }

    private void launchCombat(String enemyName, String spriteName, boolean armorRepaired) {
        stopBackgroundMusic();
        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("enemy", enemyName);
        intent.putExtra("enemySprite", spriteName);
        intent.putExtra("playerHP", armorRepaired ? 100 : 70);
        intent.putExtra("playerCosmos", armorRepaired ? 100 : 40);
        startActivity(intent);
    }

    private void stopBackgroundMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBackgroundMusic();
    }

    private void startBattleActivity() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Intent intent = new Intent(MainActivity.this, BattleActivity.class);
        intent.putExtra("playerHP", 100);
        intent.putExtra("playerCosmos", 100);
        startActivity(intent);
    }
}
