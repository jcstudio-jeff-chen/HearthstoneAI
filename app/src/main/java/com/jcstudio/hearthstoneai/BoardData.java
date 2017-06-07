package com.jcstudio.hearthstoneai;

import java.util.ArrayList;

/**
 * Created by jeffrey on 2017/6/3.
 */

public class BoardData implements Game.Observer{
    public static final int REMEMBERED = 5;
    public Game game;

    /*
    public ArrayList<ArrayList<Minion>> minions; // 7 x 5 = 35
    public int[] heroHp = new int[2]; // 2
    public ArrayList<Card> handCards; // 10 x 4 = 40
    public int handCardSize[] = new int[2]; // 2
    public int deckSize[] = new int[2]; // 2
    public int firstSide; // 1
    public int[] mana = new int[2]; // 2
    public int[] maxMana = new int[2]; // 2
    */
    public ArrayList<ArrayList<Card>> lastCards = new ArrayList<>(2); // 2 x 5 x 4 = 40

    public BoardData(Game game) {
        this.game = game;
        game.addObserver(this);
        for(int i = 0; i < 2; i++){
            lastCards.add(new ArrayList<Card>(REMEMBERED));
        }
    }


    public static int arraySize(){
        return 70 + 2 + 40 + 2 + 2 + 1 + 2 + 2 + 2*REMEMBERED*4;
    }

    public double[] createArray(int side){
        int[] sides = {side, 1-side};
        double[] output = new double[arraySize()];
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < game.minions.get(sides[i]).size(); j++){
                Minion m = game.minions.get(sides[i]).get(j);
                int index = i*35+j*5;
                output[index] = m.hasTaunt ? 1:0;
                output[index+1] = m.cost;
                output[index+2] = m.atk;
                output[index+3] = m.hp;
                output[index+4] = m.currentHp;
            }
        }
        for(int i = 0; i < 2; i++){
            output[70+i] = game.hp[sides[i]];
        }
        for(int i = 0; i < game.handCards.get(side).size(); i++){
            int index = 72 + i*4;
            Card c = game.handCards.get(side).get(i);
            output[index] = c.isCoin ? -1 : c.cost;
            output[index+1] = c.hasTaunt ? 1 : 0;
            output[index+2] = c.atk;
            output[index+3] = c.hp;
        }
        for(int i = 0; i < 2; i++){
            output[112+i] = game.handCards.get(sides[i]).size();
        }
        for(int i = 0; i < 2; i++){
            output[114+i] = game.decks.get(sides[i]).size();
        }
        output[116] = (game.firstHand == side) ? 1:0;
        for(int i = 0; i < 2; i++){
            output[117+i] = game.mana[sides[i]];
        }
        for(int i = 0; i < 2; i++){
            output[119+i] = game.maxMana[sides[i]];
        }
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < lastCards.get(sides[i]).size(); j++){
                int index = 121 + i*REMEMBERED*4 + j*4;
                Card c = lastCards.get(sides[i]).get(j);
                output[index] = c.isCoin ? -1:c.cost;
                output[index+1] = c.hasTaunt ? 1:0;
                output[index+2] = c.atk;
                output[index+3] = c.hp;
            }
        }
        return output;
    }

    @Override
    public void onDeckPrepared() {

    }

    @Override
    public void onFirstHandDetermined(int side) {

    }

    @Override
    public void onCoinGained(int side, Card coin) {

    }

    @Override
    public void onCardDraw(int side, Card card, boolean isInitialDraw) {
    }

    @Override
    public void onCardBurn(int side, Card card) {

    }

    @Override
    public void onTired(int side, int damage) {

    }

    @Override
    public void onManaChanged(int side, int mana) {

    }

    @Override
    public void onMaxManaChanged(int side, int maxMana) {

    }

    @Override
    public void onInitialDrawFinished() {

    }

    @Override
    public void onCardUsed(int side, int i, Card c) {
        ArrayList<Card> cards = lastCards.get(side);
        if(cards.size() == REMEMBERED){
            cards.remove(0);
        }
        cards.add(c);
    }

    @Override
    public void onMinionSummoned(int side, Minion minion) {

    }

    @Override
    public void onSideChanged(int side) {

    }

    @Override
    public void onHeroDamaged(int side, int damage, int hpAfterDamage) {

    }

    @Override
    public void onMinionDamaged(int side, int p, Minion m, int damage, int hpAfterDamage) {

    }

    @Override
    public void onHeroDead(int side) {

    }

    @Override
    public void onMinionDead(int side, int p, Minion m) {

    }

    @Override
    public void onMinionRested(int side, int p, Minion m) {

    }

    @Override
    public void onAttack(int side, int p1, int p2, Minion m1, Minion m2) {

    }
}
