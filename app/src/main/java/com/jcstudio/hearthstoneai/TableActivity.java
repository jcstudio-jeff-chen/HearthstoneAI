package com.jcstudio.hearthstoneai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class TableActivity extends AppCompatActivity implements Game.Observer {

    private Game game;
    private TextView[] tvHandCardSize = new TextView[2];
    private TextView[] tvMana = new TextView[2];
    private TextView[] tvMaxMana = new TextView[2];
    private TextView[] tvDeckSize = new TextView[2];
    private Button bFinish;
    private Button[] bCard = new Button[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        findViews();

        game = new Game();
        game.observer = this;
        game.createDeck();
        game.shuffle();
        game.initialDraw();
    }

    private void findViews(){
        tvHandCardSize[0] = (TextView) findViewById(R.id.tv_hand_card_size_0);
        tvHandCardSize[1] = (TextView) findViewById(R.id.tv_hand_card_size_1);
        tvMana[0] = (TextView) findViewById(R.id.tv_mana_0);
        tvMana[1] = (TextView) findViewById(R.id.tv_mana_1);
        tvMaxMana[0] = (TextView) findViewById(R.id.tv_max_mana_0);
        tvMaxMana[1] = (TextView) findViewById(R.id.tv_max_mana_1);
        tvDeckSize[0] = (TextView) findViewById(R.id.tv_deck_size_0);
        tvDeckSize[1] = (TextView) findViewById(R.id.tv_deck_size_1);
        bFinish = (Button) findViewById(R.id.b_finish);
        bCard[0] = (Button) findViewById(R.id.b_card_0);
        bCard[1] = (Button) findViewById(R.id.b_card_1);
        bCard[2] = (Button) findViewById(R.id.b_card_2);
        bCard[3] = (Button) findViewById(R.id.b_card_3);
        bCard[4] = (Button) findViewById(R.id.b_card_4);
        bCard[5] = (Button) findViewById(R.id.b_card_5);
        bCard[6] = (Button) findViewById(R.id.b_card_6);
        bCard[7] = (Button) findViewById(R.id.b_card_7);
        bCard[8] = (Button) findViewById(R.id.b_card_8);
        bCard[9] = (Button) findViewById(R.id.b_card_9);
    }

    @Override
    public void onDeckPrepared() {
        for(int i = 0; i < 2; i++){
            tvDeckSize[i].setText(String.valueOf(game.decks.get(i).size()));
        }
        for(int i = 0; i < 10; i++){
            bCard[i].setVisibility(View.GONE);
        }
    }

    @Override
    public void onFirstHandDetermined(int side) {
        bFinish.setEnabled(side == 1);
        bFinish.setText(side == 1 ? "結束回合" : "對手回合");
    }

    @Override
    public void onCoinGained(int side) {
        tvHandCardSize[side].setText(String.valueOf(game.handCards.get(side).size()));
    }

    @Override
    public void onCardDraw(int side, Card card) {
        tvDeckSize[side].setText(String.valueOf(game.decks.get(side).size()));
        tvHandCardSize[side].setText(String.valueOf(game.handCards.get(side).size()));
        Button drawn = bCard[game.handCards.get(side).size()-1];
        drawn.setVisibility(View.VISIBLE);
        drawn.setText(card.shownName());
    }

    @Override
    public void onCardBurn(int side, Card card) {

    }

    @Override
    public void onTired(int side, int damage) {

    }

    @Override
    public void onTurn(int newSide) {

    }
}
