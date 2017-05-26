package com.jcstudio.hearthstoneai;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playWithAI(View v){
        Intent intent = new Intent(this, TableActivity.class);
        startActivity(intent);
    }

    public void aiLearn(View v){
        Intent intent = new Intent(this, TableActivity.class);
        startActivity(intent);
    }

    public void showGameGuide(View v){
        new AlertDialog.Builder(this).setTitle(R.string.game_guide).setMessage(R.string.game_guide_content).show();
    }
}
