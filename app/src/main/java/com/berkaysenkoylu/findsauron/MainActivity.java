package com.berkaysenkoylu.findsauron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextView highscoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        highscoreText = findViewById(R.id.highscoreText);
        sharedPreferences = this.getSharedPreferences("com.berkaysenkoylu.findsauron", Context.MODE_PRIVATE);
        int savedScore = sharedPreferences.getInt("score", 0);
        highscoreText.setText("Highscore: " + savedScore);
    }

    public void onStartGameHandler(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }
}