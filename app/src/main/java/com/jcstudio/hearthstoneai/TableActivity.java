package com.jcstudio.hearthstoneai;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private Button[][] bMinion = new Button[2][7];
    private LinearLayout[] minionField = new LinearLayout[2];
    private LinearLayout cardField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        findViews();

        game = new Game();
        game.observer = this;
        game.resetMana();
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
        bMinion[0][0] = (Button) findViewById(R.id.b_minion_0_0);
        bMinion[0][1] = (Button) findViewById(R.id.b_minion_0_1);
        bMinion[0][2] = (Button) findViewById(R.id.b_minion_0_2);
        bMinion[0][3] = (Button) findViewById(R.id.b_minion_0_3);
        bMinion[0][4] = (Button) findViewById(R.id.b_minion_0_4);
        bMinion[0][5] = (Button) findViewById(R.id.b_minion_0_5);
        bMinion[0][6] = (Button) findViewById(R.id.b_minion_0_6);
        bMinion[1][0] = (Button) findViewById(R.id.b_minion_1_0);
        bMinion[1][1] = (Button) findViewById(R.id.b_minion_1_1);
        bMinion[1][2] = (Button) findViewById(R.id.b_minion_1_2);
        bMinion[1][3] = (Button) findViewById(R.id.b_minion_1_3);
        bMinion[1][4] = (Button) findViewById(R.id.b_minion_1_4);
        bMinion[1][5] = (Button) findViewById(R.id.b_minion_1_5);
        bMinion[1][6] = (Button) findViewById(R.id.b_minion_1_6);
        minionField[0] = (LinearLayout) findViewById(R.id.minion_field_0);
        minionField[1] = (LinearLayout) findViewById(R.id.minion_field_1);
        cardField = (LinearLayout) findViewById(R.id.card_field);
    }

    public void finishTurn(View v){
        changeSide();
    }

    public void useCard(View v){
        if(game.turnSide != 1){
            return;
        }
        game.useCard(cardField.indexOfChild(v));
    }

    private void changeSide(){
        game.changeSide();
    }

    private void refreshFinishButton(int side){
        bFinish.setEnabled(side == 1);
        bFinish.setText(side == 1 ? "結束回合" : "對手回合");
    }

    private void actionForNewTurn(){
        game.setManaForNewTurn();
        game.drawCardForNewTurn();
        if(game.turnSide == 0){
            changeSide();
        }
    }

    @Override
    public void onDeckPrepared() {
        for(int i = 0; i < 2; i++){
            tvDeckSize[i].setText(String.valueOf(game.decks.get(i).size()));
        }
        for(int i = 0; i < 10; i++){
            bCard[i].setVisibility(View.GONE);
        }
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 7; j++){
                bMinion[i][j].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFirstHandDetermined(int side) {
        refreshFinishButton(side);
    }

    @Override
    public void onCoinGained(int side, Card coin) {
        tvHandCardSize[side].setText(String.valueOf(game.handCards.get(side).size()));
        if(side == 1) {
            Button drawn = bCard[game.handCards.get(side).size() - 1];
            drawn.setVisibility(View.VISIBLE);
            drawn.setText(coin.shownName());
            drawn.getBackground().setColorFilter(new PorterDuffColorFilter(coin.filterColor(), PorterDuff.Mode.SRC_ATOP));
            drawn.setEnabled(game.isUsable(1, coin));
        }
    }

    @Override
    public void onCardDraw(int side, Card card) {
        tvDeckSize[side].setText(String.valueOf(game.decks.get(side).size()));
        tvHandCardSize[side].setText(String.valueOf(game.handCards.get(side).size()));
        if(side == 1) {
            Button drawn = bCard[game.handCards.get(side).size() - 1];
            drawn.setVisibility(View.VISIBLE);
            drawn.setText(card.shownName());
            drawn.getBackground().setColorFilter(new PorterDuffColorFilter(card.filterColor(), PorterDuff.Mode.SRC_ATOP));
            drawn.setEnabled(game.isUsable(1, card));
        }
    }

    @Override
    public void onCardBurn(int side, Card card) {

    }

    @Override
    public void onTired(int side, int damage) {

    }

    @Override
    public void onManaChanged(int side, int mana) {
        tvMana[side].setText(String.valueOf(mana));
        if(side == 1) {
            ArrayList<Card> handCard = game.handCards.get(1);
            for (int i = 0; i < handCard.size(); i++) {
                Card c = handCard.get(i);
                bCard[i].setEnabled(game.isUsable(1, c));
            }
        }
    }

    @Override
    public void onMaxManaChanged(int side, int maxMana) {
        tvMaxMana[side].setText(String.valueOf(maxMana));
    }

    @Override
    public void onInitialDrawFinished() {
        actionForNewTurn();
    }

    @Override
    public void onCardUsed(int side, int n, Card c) {
        tvHandCardSize[side].setText(String.valueOf(game.handCards.get(side).size()));
        if(side == 1) {
            Button b = bCard[n];
            for(int i = n; i < 9; i++){
                bCard[i] = bCard[i+1];
            }
            bCard[9] = b;
            cardField.removeView(b);
            cardField.addView(b);
            b.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMinionSummoned(int side, Minion minion) {
        Button b = bMinion[side][game.minions.get(side).size()-1];
        b.setVisibility(View.VISIBLE);
        b.setText(minion.shownName());
        b.getBackground().setColorFilter(new PorterDuffColorFilter(minion.filterColor(), PorterDuff.Mode.SRC_ATOP));
    }

    @Override
    public void onSideChanged(int side) {
        refreshFinishButton(side);
        if(side != 1){
            for(Button b : bCard){
                b.setEnabled(false);
            }
        }
        actionForNewTurn();
    }
}
