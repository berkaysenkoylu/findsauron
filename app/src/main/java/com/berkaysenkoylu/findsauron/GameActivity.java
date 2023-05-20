package com.berkaysenkoylu.findsauron;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    final int gameDuration = 30;
    int currentVisibleIndex, previousVisibleIndex;
    int score = 0;
    int comboPoint = 1;
    TableLayout imageContainer;
    ArrayList<View> imageList = new ArrayList<>();
    Random rand;
    TextView timerText;
    TextView scoreText;
    TextView comboText;
    CountDownTimer gamePlayLoop;
    boolean canGetScore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPreferences = this.getSharedPreferences("com.berkaysenkoylu.findsauron", Context.MODE_PRIVATE);

        timerText = findViewById(R.id.timerText);
        scoreText = findViewById(R.id.scoreText);
        comboText = findViewById(R.id.comboText);
        imageContainer = findViewById(R.id.tableLayout);

        // Get the image list
        imageList = getViewsByTag(imageContainer, "image");

        // Get a random number between 0 and the size of the list
        rand = new Random();
        int randomNum = getRandomNumber(imageList.size());
        currentVisibleIndex = randomNum;
        previousVisibleIndex = currentVisibleIndex;

        // Make that image at the random index visible
        imageList.get(randomNum).setVisibility(View.VISIBLE);

        scoreText.setText("Score: " + score);
        comboText.setText("Combo: x" + comboPoint);

        // Start the game
        gamePlayLoop = getGameLoop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (gamePlayLoop != null) {
            gamePlayLoop.cancel();
        }
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    private int getRandomNumber(int max) {
        // Return a random number between 0 and 9 (exclusive)
        return rand.nextInt((max - 0) + 0) + 0;
    }

    public void shuffleTheImages() {
        int randomInteger = currentVisibleIndex;

        while (randomInteger == currentVisibleIndex) {
            randomInteger = getRandomNumber(imageList.size());
        }

        previousVisibleIndex = currentVisibleIndex;
        currentVisibleIndex = randomInteger;

        // Make the previously visible image INVISIBLE
        imageList.get(previousVisibleIndex).setAlpha(0);

        // Make the current invisible image VISIBLE
        imageList.get(currentVisibleIndex).setAlpha(1);

        canGetScore = true;
    }

    public CountDownTimer getGameLoop(int... timerLeftover) {
        return new CountDownTimer((gameDuration + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);

                shuffleTheImages();
            }

            @Override
            public void onFinish() {
                // When the time is up (GAME OVER)
                gameOverAlertHandler();
                gamePlayLoop.cancel();
            }
        }.start();
    }

    public void onImagePressed(View view) {
        if (canGetScore && view.getAlpha() == 1) {
            score += comboPoint++;
            canGetScore = false;
            scoreText.setText("Score: " + score);
        } else {
            comboPoint = 1;
        }
        comboText.setText("Combo: x" + comboPoint);
    }

    public void gameOverAlertHandler() {
        int savedScore = sharedPreferences.getInt("score", 0);
        if (savedScore < score) {
            sharedPreferences.edit().putInt("score", score).apply();
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Game Over!");
        alert.setMessage("Do you want to play again?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Restart the game
                Toast.makeText(GameActivity.this, "Yes button is pressed", Toast.LENGTH_LONG).show();
                score = 0;
                comboPoint = 1;
                scoreText.setText("Score: " + score);
                comboText.setText("Combo: x" + comboPoint);
                canGetScore = true;
                gamePlayLoop = getGameLoop();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Go back to the previous screen
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        alert.show();
    }
}