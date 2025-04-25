package com.example.saintseiya.activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.saintseiya.R;
import com.example.saintseiya.dialogs.InventoryDialogFragment;
import com.example.saintseiya.model.InventoryItem;
import com.example.saintseiya.model.SeiyaAttack;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class BattleActivity extends AppCompatActivity {

    private ImageView playerSprite, enemySprite;
    private MediaPlayer battleMusic;

    private TextView statusText, playerHpText, playerCosmosText, enemyHpText;
    private ProgressBar hpBar, cosmosBar, enemyHpBar;
    private Button btnAttack1, btnAttack2, btnAttack3, btnFlee, btnInventory;

    private int playerHP;
    private int playerCosmos;
    private int enemyHP = 100;
    private String enemyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        battleMusic = MediaPlayer.create(this, R.raw.battle_music);
        battleMusic.setLooping(true);
        battleMusic.start();

        enemySprite = findViewById(R.id.enemySprite);
        statusText = findViewById(R.id.statusText);
        playerHpText = findViewById(R.id.playerHpText);
        playerCosmosText = findViewById(R.id.playerCosmosText);
        enemyHpText = findViewById(R.id.enemyHpText);
        hpBar = findViewById(R.id.hpBar);
        cosmosBar = findViewById(R.id.cosmosBar);
        enemyHpBar = findViewById(R.id.enemyHpBar);
        btnAttack1 = findViewById(R.id.btnAttack1);
        btnAttack2 = findViewById(R.id.btnAttack2);
        btnAttack3 = findViewById(R.id.btnAttack3);
        btnFlee = findViewById(R.id.btnFlee);
        btnInventory = findViewById(R.id.btnInventory);

        Intent intent = getIntent();
        enemyName = intent.getStringExtra("enemy");
        String enemySpriteName = intent.getStringExtra("enemySprite");
        playerHP = intent.getIntExtra("playerHP", 100);
        playerCosmos = intent.getIntExtra("playerCosmos", 100);
        enemyHP = 100;

        Log.d("BattleActivity", "Nom de l'ennemi : " + enemyName + ", Nom du sprite : " + enemySpriteName);
        Log.d("BattleActivity", "HP joueur : " + playerHP + ", Cosmos joueur : " + playerCosmos);

        if (enemySpriteName != null && !enemySpriteName.isEmpty()) {
            int spriteId = getResources().getIdentifier(enemySpriteName, "drawable", getPackageName());
            if (spriteId != 0) {
                enemySprite.setImageResource(spriteId);
            } else {
                Log.e("BattleActivity", "Sprite introuvable pour l'ennemi : " + enemyName + ". Utilisation de l'image par défaut.");
                enemySprite.setImageResource(R.drawable.enemy_default);
            }
        } else {
            Log.e("BattleActivity", "Nom du sprite non fourni pour l'ennemi : " + enemyName + ". Utilisation de l'image par défaut.");
            enemySprite.setImageResource(R.drawable.enemy_default);
        }

        statusText.setText("Le chevalier d'or " + enemyName + " se prépare au combat !");
        updateBars();

        btnAttack1.setOnClickListener(v -> performAttack(new SeiyaAttack("Météore", 10, 10)));
        btnAttack2.setOnClickListener(v -> performAttack(new SeiyaAttack("Comète", 25, 20)));
        btnAttack3.setOnClickListener(v -> performAttack(new SeiyaAttack("Galaxian", 50, 40)));

        btnFlee.setOnClickListener(v -> {
            statusText.setText("Tu prends la fuite !");
            finishBattle("mort_fuite_" + formatEnemyName(enemyName));
        });

        btnInventory.setOnClickListener(v -> {
            List<InventoryItem> inventory = new ArrayList<>();
            inventory.add(new InventoryItem("Potion", 3));
            inventory.add(new InventoryItem("Élixir", 2));
            inventory.add(new InventoryItem("Autre objet", 1));

            InventoryDialogFragment dialog = new InventoryDialogFragment();
            dialog.setInventoryItems(inventory);
            dialog.setOnItemSelectedListener(item -> {
                if (item.getName().equals("Potion")) {
                    if (item.getQuantity() > 0) {
                        item.decreaseQuantity();
                        usePotion();
                    } else {
                        statusText.setText("Vous n'avez plus de potions !");
                    }
                } else {
                    statusText.setText("Cet objet ne peut pas être utilisé !");
                }
            });
            dialog.show(getSupportFragmentManager(), "inventory");
        });
    }

    private void performAttack(SeiyaAttack atk) {
        if (playerCosmos < atk.getCost()) {
            statusText.setText("Pas assez de cosmos pour " + atk.getName() + " !");
            return;
        }

        disableButtons();
        int oldPlayerCosmos = playerCosmos;
        playerCosmos -= atk.getCost();

        Log.d("BattleActivity", "Attaque : " + atk.getName() + ", Dégâts : " + atk.getDamage());

        vibratePhone(200);

        animateProgressBar(cosmosBar, oldPlayerCosmos, playerCosmos, playerCosmosText, "Cosmos");

        btnAttack1.postDelayed(() -> {
            int oldEnemyHp = enemyHP;
            enemyHP = Math.max(0, enemyHP - atk.getDamage());

            Log.d("BattleActivity", "HP ennemi avant : " + oldEnemyHp + ", après : " + enemyHP);

            showDamage(findViewById(R.id.enemyDamageText), atk.getDamage());

            animateProgressBar(enemyHpBar, oldEnemyHp, enemyHP, enemyHpText, (enemyName != null ? enemyName : "Ennemi") + " HP");

            if (enemyHP == 0) {
                statusText.setText("Victoire !");
                finishBattle("victoire_" + formatEnemyName(enemyName));
            } else {
                statusText.setText("L'ennemi a encore " + enemyHP + " HP !");
                btnAttack1.postDelayed(() -> {
                    enemyAttack();
                    enableButtons();
                }, 1500);
            }
        }, 1000);
    }

    private void enemyAttack() {
        int damage = 20;
        int oldPlayerHp = playerHP;
        playerHP = Math.max(0, playerHP - damage);

        Log.d("BattleActivity", "L'ennemi attaque ! Dégâts infligés : " + damage);
        Log.d("BattleActivity", "HP du joueur avant : " + oldPlayerHp + ", après : " + playerHP);

        showDamage(findViewById(R.id.playerDamageText), damage);

        animateProgressBar(hpBar, oldPlayerHp, playerHP, playerHpText, "HP");

        if (playerHP == 0) {
            statusText.setText("Vous avez été vaincu !");
            finishBattle("mort_" + formatEnemyName(enemyName));
        } else {
            statusText.setText("L'ennemi vous a infligé " + damage + " dégâts !");
        }

        vibratePhone(300);
    }

    private String formatEnemyName(String name) {
        return Normalizer.normalize(name.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("\\s+", "_");
    }

    private void updateBars() {
        hpBar.setMax(100);
        cosmosBar.setMax(100);
        enemyHpBar.setMax(100);

        hpBar.setProgress(playerHP);
        cosmosBar.setProgress(playerCosmos);
        enemyHpBar.setProgress(enemyHP);

        playerHpText.setText("HP : " + playerHP + " / 100");
        playerCosmosText.setText("Cosmos : " + playerCosmos + " / 100");
        enemyHpText.setText((enemyName != null ? enemyName : "Ennemi") + " HP : " + enemyHP + " / 100");
    }

    private void disableButtons() {
        btnAttack1.setEnabled(false);
        btnAttack2.setEnabled(false);
        btnAttack3.setEnabled(false);
        btnFlee.setEnabled(false);
        btnInventory.setEnabled(false);
    }

    private void enableButtons() {
        btnAttack1.setEnabled(true);
        btnAttack2.setEnabled(true);
        btnAttack3.setEnabled(true);
        btnFlee.setEnabled(true);
        btnInventory.setEnabled(true);
    }

    private void animateProgressBar(ProgressBar bar, int from, int to, TextView textView, String label) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            bar.setProgress(currentValue);
            textView.setText(label + " : " + currentValue + " / " + bar.getMax());
        });
        animator.start();
    }

    private void shakeView(View view) {
        view.animate()
            .translationXBy(10)
            .setDuration(50)
            .withEndAction(() -> view.animate()
                .translationXBy(-20)
                .setDuration(50)
                .withEndAction(() -> view.animate()
                    .translationXBy(10)
                    .setDuration(50)))
            .start();
    }

    private void vibratePhone(int durationMs) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    private void finishBattle(String blockId) {
        disableButtons();
        btnAttack1.postDelayed(() -> {
            Intent intent = new Intent(this, blockId.startsWith("mort") ? GameOverActivity.class : MainActivity.class);
            intent.putExtra("blockId", blockId);
            intent.putExtra("enemy", enemyName);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void endBattle(String nextBlockId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("blockId", nextBlockId);
        startActivity(intent);
        finish();
    }

    private void usePotion() {
        int potionCosmosGain = 30;
        int oldPlayerCosmos = playerCosmos;
        playerCosmos = Math.min(playerCosmos + potionCosmosGain, 100);

        Log.d("BattleActivity", "Potion utilisée. Cosmos avant : " + oldPlayerCosmos + ", après : " + playerCosmos);

        animateProgressBar(cosmosBar, oldPlayerCosmos, playerCosmos, playerCosmosText, "Cosmos");

        statusText.setText("Vous avez utilisé une potion ! Cosmos + " + potionCosmosGain);
    }

    private void showDamage(TextView damageText, int damage) {
        damageText.setText("-" + damage);
        damageText.setVisibility(View.VISIBLE);

        damageText.animate()
            .alpha(0f)
            .setDuration(1500)
            .withEndAction(() -> {
                damageText.setVisibility(View.GONE);
                damageText.setAlpha(1f);
            })
            .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (battleMusic != null) {
            battleMusic.stop();
            battleMusic.release();
            battleMusic = null;
        }
    }
}
