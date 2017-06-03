package com.jcstudio.hearthstoneai;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView[][] attackBubble = new TextView[2][8];
    private int selectedMinion;
    private FrameLayout[][] slot = new FrameLayout[2][7];
    private Button[] bHero = new Button[2];
    private AI ai;
    private boolean isBusy = false;
    private BoardData boardData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        findViews();

        for(TextView[] tvs : attackBubble){
            for(TextView tv : tvs){
                tv.setVisibility(View.INVISIBLE);
            }
        }

        ai = new AI();

        game = new Game();
        boardData = new BoardData(game);
        game.addObserver(this);
        game.addObserver(boardData);
        game.resetMana();
        game.createDeck();
        game.shuffle();
        game.initialDraw();
        setSelectedMinion(-1);
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
        attackBubble[0][0] = (TextView) findViewById(R.id.attack_bubble_0_0);
        attackBubble[0][1] = (TextView) findViewById(R.id.attack_bubble_0_1);
        attackBubble[0][2] = (TextView) findViewById(R.id.attack_bubble_0_2);
        attackBubble[0][3] = (TextView) findViewById(R.id.attack_bubble_0_3);
        attackBubble[0][4] = (TextView) findViewById(R.id.attack_bubble_0_4);
        attackBubble[0][5] = (TextView) findViewById(R.id.attack_bubble_0_5);
        attackBubble[0][6] = (TextView) findViewById(R.id.attack_bubble_0_6);
        attackBubble[0][7] = (TextView) findViewById(R.id.attack_bubble_0_7);
        attackBubble[1][0] = (TextView) findViewById(R.id.attack_bubble_1_0);
        attackBubble[1][1] = (TextView) findViewById(R.id.attack_bubble_1_1);
        attackBubble[1][2] = (TextView) findViewById(R.id.attack_bubble_1_2);
        attackBubble[1][3] = (TextView) findViewById(R.id.attack_bubble_1_3);
        attackBubble[1][4] = (TextView) findViewById(R.id.attack_bubble_1_4);
        attackBubble[1][5] = (TextView) findViewById(R.id.attack_bubble_1_5);
        attackBubble[1][6] = (TextView) findViewById(R.id.attack_bubble_1_6);
        attackBubble[1][7] = (TextView) findViewById(R.id.attack_bubble_1_7);
        slot[0][0] = (FrameLayout) findViewById(R.id.slot_0_0);
        slot[0][1] = (FrameLayout) findViewById(R.id.slot_0_1);
        slot[0][2] = (FrameLayout) findViewById(R.id.slot_0_2);
        slot[0][3] = (FrameLayout) findViewById(R.id.slot_0_3);
        slot[0][4] = (FrameLayout) findViewById(R.id.slot_0_4);
        slot[0][5] = (FrameLayout) findViewById(R.id.slot_0_5);
        slot[0][6] = (FrameLayout) findViewById(R.id.slot_0_6);
        slot[1][0] = (FrameLayout) findViewById(R.id.slot_1_0);
        slot[1][1] = (FrameLayout) findViewById(R.id.slot_1_1);
        slot[1][2] = (FrameLayout) findViewById(R.id.slot_1_2);
        slot[1][3] = (FrameLayout) findViewById(R.id.slot_1_3);
        slot[1][4] = (FrameLayout) findViewById(R.id.slot_1_4);
        slot[1][5] = (FrameLayout) findViewById(R.id.slot_1_5);
        slot[1][6] = (FrameLayout) findViewById(R.id.slot_1_6);
        bHero[0] = (Button) findViewById(R.id.b_hero_0);
        bHero[1] = (Button) findViewById(R.id.b_hero_1);
    }

    public void finishTurn(View v){
        if(isBusy){
            return;
        }
        changeSide();
    }

    public void useCard(View v){
        if(isBusy){
            return;
        }
        if(game.turnSide != 1){
            return;
        }
        game.useCard(cardField.indexOfChild(v));
    }

    public void selectMinion(View v){
        if(isBusy){
            return;
        }
        int n = minionField[1].indexOfChild((View) v.getParent());
        if(n == selectedMinion){
            bMinion[1][n].getBackground().setColorFilter(game.minions.get(1).get(n).filterColor(), PorterDuff.Mode.SRC_ATOP);
            setSelectedMinion(-1);
        } else {
            if(selectedMinion != -1) {
                bMinion[1][selectedMinion].getBackground().setColorFilter(game.minions.get(1).get(selectedMinion).filterColor(), PorterDuff.Mode.SRC_ATOP);
            }
            bMinion[1][n].getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            setSelectedMinion(n);
        }
    }

    public void attackMinion(View v){
        if(isBusy){
            return;
        }
        int n = minionField[0].indexOfChild((View) v.getParent());
        game.attack(1, selectedMinion, n);
        setSelectedMinion(-1);
    }

    public void attackHero(View v){
        if(isBusy){
            return;
        }
        game.attack(1, selectedMinion, 7);
        setSelectedMinion(-1);
    }

    private void changeSide(){
        game.changeSide();
    }

    private void setSelectedMinion(int selectedMinion){
        this.selectedMinion = selectedMinion;
        onSelectionChanged(selectedMinion);
    }

    private void onSelectionChanged(int selectedMinion){
        if(selectedMinion == -1){
            for(Button b : bMinion[0]){
                b.setEnabled(false);
            }
            bHero[0].setEnabled(false);
        } else {
            for(int i = 0; i < 7; i++){
                bMinion[0][i].setEnabled(game.isAttackable(0, i));
            }
            bHero[0].setEnabled(game.isAttackable(0, 7));
        }
    }

    private void refreshFinishButton(int side){
        bFinish.setEnabled(side == 1);
        bFinish.setText(side == 1 ? "結束回合" : "對手回合");
    }

    private void actionForNewTurn(){
        if(game.turnSide == 0){
            ArrayList<Integer> actions = ai.getActionList(boardData.createArray(0));
            for(int code : actions){
                if(game.isActionAvailable(0, code)){
                    game.performAction(code);
                    break;
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    actionForNewTurn();
                }
            }, 1100);
        }
    }

    private void showAttackBubble(int side, int p, int damage){
        final TextView tv = attackBubble[side][p];
        String damageShown = "-" + damage;
        tv.setText(damageShown);
        tv.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.damage_bubble);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tv.startAnimation(anim);
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
                slot[i][j].setVisibility(View.GONE);
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
    public void onCardDraw(int side, Card card, boolean isInitialDraw) {
        tvDeckSize[side].setText(String.valueOf(game.decks.get(side).size()));
        tvHandCardSize[side].setText(String.valueOf(game.handCards.get(side).size()));
        if(side == 1) {
            Button drawn = bCard[game.handCards.get(side).size() - 1];
            drawn.setVisibility(View.VISIBLE);
            drawn.setText(card.shownName());
            drawn.getBackground().setColorFilter(new PorterDuffColorFilter(card.filterColor(), PorterDuff.Mode.SRC_ATOP));
            drawn.setEnabled(game.isUsable(1, card));
            if(!isInitialDraw) {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isBusy = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                drawn.startAnimation(animation);
                isBusy = true;
            }
        }
    }

    @Override
    public void onCardBurn(int side, Card card) {
        Toast.makeText(this, Game.NAMES[side] + "手牌過多，一張 " + card + " 被摧毀了", Toast.LENGTH_SHORT).show();
        tvDeckSize[side].setText(String.valueOf(game.decks.get(side).size()));
    }

    @Override
    public void onTired(int side, int damage) {
        Toast.makeText(this, Game.NAMES[side] + "牌堆空了，受到疲勞傷害 " + damage + " 點", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onManaChanged(int side, int mana) {
        tvMana[side].setText(String.valueOf(mana));
        if(side == 1) {
            for (int i = 0; i < 10; i++) {
                bCard[i].setEnabled(game.isUsable(1, i));
            }
        }
    }

    @Override
    public void onMaxManaChanged(int side, int maxMana) {
        tvMaxMana[side].setText(String.valueOf(maxMana));
    }

    @Override
    public void onInitialDrawFinished() {
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
        b.setText(minion.shownName());
        b.getBackground().setColorFilter(new PorterDuffColorFilter(minion.filterColor(), PorterDuff.Mode.SRC_ATOP));
        b.setEnabled(game.isMovable(side, minion));
        FrameLayout s = slot[side][game.minions.get(side).size()-1];
        s.setVisibility(View.VISIBLE);
        if(side == 1) {
            if (game.minions.get(side).size() == 7) {
                for(int i = 0; i < 10; i++){
                    bCard[i].setEnabled(game.isUsable(1, i));
                }
            }
        }
    }

    @Override
    public void onSideChanged(int side) {
        refreshFinishButton(side);
        if(side != 1){
            for(Button b : bCard){
                b.setEnabled(false);
            }
            for(Button[] bs : bMinion){
                for(Button b : bs) {
                    b.setEnabled(false);
                }
            }
            for(int i = 0; i < game.minions.get(1).size(); i++){
                bMinion[1][i].getBackground().setColorFilter(game.minions.get(1).get(i).filterColor(), PorterDuff.Mode.SRC_ATOP);
            }
            setSelectedMinion(-1);
        } else {
            for(int i = 0; i < 10; i++) {
                bCard[i].setEnabled(game.isUsable(1, i));
            }
            for(int i = 0; i < 7; i++){
                bMinion[1][i].setEnabled(game.isMovable(1, i));
            }
        }
        actionForNewTurn();
    }

    @Override
    public void onHeroDamaged(int side, int damage, int hpAfterDamage) {
        showAttackBubble(side, 7, damage);
        bHero[side].setText(String.valueOf(hpAfterDamage));
    }

    @Override
    public void onMinionDamaged(int side, int p, Minion m, int damage, int hpAfterDamage) {
        showAttackBubble(side, p, damage);
        bMinion[side][p].setText(m.shownName());
    }

    @Override
    public void onHeroDead(int side) {

    }

    @Override
    public void onMinionDead(final int side, final int p, Minion m) {
        final Button b = bMinion[side][p];
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout s = slot[side][p];
                TextView ab = attackBubble[side][p];
                for(int i = p; i < 6; i++){
                    slot[side][i] = slot[side][i+1];
                    bMinion[side][i] = bMinion[side][i+1];
                    attackBubble[side][i] = attackBubble[side][i+1];
                }
                slot[side][6] = s;
                bMinion[side][6] = b;
                attackBubble[side][6] = ab;
                minionField[side].removeView(s);
                minionField[side].addView(s);
                s.setVisibility(View.GONE);
                isBusy = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        isBusy = true;
        b.startAnimation(anim);

        if(side == 1) {
            for (int i = 0; i < 10; i++) {
                bCard[i].setEnabled(game.isUsable(1, i));
            }
        }
    }

    @Override
    public void onMinionRested(int side, int p, Minion m) {
        if(side != 1){
            return;
        }
        if(p == selectedMinion){
            setSelectedMinion(-1);
            bMinion[1][p].getBackground().setColorFilter(m.filterColor(), PorterDuff.Mode.SRC_ATOP);
        }
        bMinion[1][p].setEnabled(false);
    }

    @Override
    public void onAttack(int side, final int p1, int p2, final Minion m1, Minion m2) {
        if(side == 0) {
            bMinion[0][p1].getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (m1.currentHp > 0) {
                        bMinion[0][p1].getBackground().setColorFilter(m1.filterColor(), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }, 600);
        }
    }
}
