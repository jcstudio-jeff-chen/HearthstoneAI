package com.jcstudio.hearthstoneai;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by jeffrey on 2017/5/26.
 */

public class Game {
    public static final int UPPER = 0;
    public static final int DOWN = 1;
    public static final String NAMES[] = {"上家", "下家"};

    public ArrayList<ArrayList<Card>> decks = new ArrayList<>(2);
    public ArrayList<ArrayList<Card>> handCards = new ArrayList<>(2);
    public ArrayList<ArrayList<Minion>> minions = new ArrayList<>(2);

    public int turnSide;
    private int[] hp = {30, 30};
    public int[] mana = {0, 0};
    private int[] maxMana = {0, 0};
    private int[] tiredAmount = {0, 0};

    public Observer observer;

    public Game(){
        for(int i = 0; i < 2; i++) {
            decks.add(new ArrayList<Card>(30));
            handCards.add(new ArrayList<Card>(10));
            minions.add(new ArrayList<Minion>(7));
        }
    }

    public void createDeck(){
        Log.d("Game", "產生套牌");
        String name = NAMES[0];
        for(ArrayList<Card> deck : decks){
            deck.clear();
            for(int i = 0; i < 10; i++){
                for(int j = 0; j < 3; j++){
                    Card c = Card.randomCard(i+1, 0.3);
                    deck.add(c);
                    Log.d("Game", name + ": " + c);
                }
            }
            name = NAMES[1];
        }
    }

    public void shuffle(){
        Log.d("Game", "洗牌");
        for(ArrayList<Card> deck : decks){
            Collections.shuffle(deck);
        }
        if(observer != null){
            observer.onDeckPrepared();
        }
    }

    public void initialDraw(){
        Random r = new Random();
        turnSide = r.nextInt(2);
        Log.d("Game", NAMES[turnSide] + "先手");
        if(observer != null){
            observer.onFirstHandDetermined(turnSide);
        }
        for(ArrayList<Card> handCard : handCards){
            handCard.clear();
        }
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                drawCard(i);
            }
        }
        Log.d("Game", "後手多抽一張");
        drawCard(1-turnSide);
        Log.d("Game", "後手獲得幸運幣");
        Card coin = new Card();
        handCards.get(1-turnSide).add(coin);
        if(observer != null){
            observer.onCoinGained(1-turnSide, coin);
            observer.onInitialDrawFinished();
        }
    }

    public void drawCard(int side){
        ArrayList<Card> deck = decks.get(side);
        ArrayList<Card> handCard = handCards.get(side);
        if(deck.isEmpty()){
            tiredAmount[side]++;
            Log.d("Game", NAMES[side] + "疲勞傷害 " + tiredAmount[side] + " 點");
            if(observer != null){
                observer.onTired(side, tiredAmount[side]);
            }
            return;
        }
        Card c = deck.remove(0);
        if(handCard.size() == 10){
            Log.d("Game", NAMES[side] + "爆牌: " + c);
            if(observer != null){
                observer.onCardBurn(side, c);
            }
            return;
        }

        Log.d("Game", NAMES[side] + "抽牌: " + c);
        handCard.add(c);
        if(observer != null){
            observer.onCardDraw(side, c);
        }
    }

    public void changeSide(){
        Log.d("Game", NAMES[1-turnSide] + "回合");
        turnSide = 1-turnSide;
        if(observer != null){
            observer.onSideChanged(turnSide);
        }
    }

    public void setMaxMana(int side, int value){
        Log.d("Game", NAMES[side] + "水晶上限由 " + maxMana[side] + " 設為 " + value);
        if(value != maxMana[side]){
            maxMana[side] = value;
            if(observer != null){
                observer.onMaxManaChanged(side, value);
            }
        }
    }

    public void setMana(int side, int value){
        Log.d("Game", NAMES[side] + "水晶由 " + mana[side] + " 設為 " + value);
        if(value != mana[side]){
            mana[side] = value;
            if(observer != null){
                observer.onManaChanged(side, value);
            }
        }
    }

    public void resetMana(){
        for(int i = 0; i < 2; i++){
            setMaxMana(i, 0);
            setMana(i, 0);
        }
    }

    public void fillMana(int side){
        setMana(side, maxMana[side]);
    }

    public void setManaForNewTurn(){
        int newMaxMana = Math.min(maxMana[turnSide]+1, 10);
        setMaxMana(turnSide, newMaxMana);
        fillMana(turnSide);
    }

    public void drawCardForNewTurn(){
        drawCard(turnSide);
    }

    public boolean isUsable(int side, Card c){
        if(side != turnSide){
            return false;
        }

        if(c.isCoin){
            return true;
        }

        if(minions.get(side).size() == 7){
            return false;
        }

        return c.cost <= mana[side];
    }

    public void useCard(int i){
        Card c = handCards.get(turnSide).remove(i);
        Log.d("Game", NAMES[turnSide] + "使用第 " + i + " 張手牌：" + c);
        if(observer != null){
            observer.onCardUsed(turnSide, i, c);
        }
        if(c.isCoin){
            int newMana = Math.min(mana[turnSide] + 1, 10);
            setMana(turnSide, newMana);
            return;
        }
        setMana(turnSide, mana[turnSide] - c.cost);
        Minion minion = new Minion(c);
        putMinion(turnSide, minion);
    }

    private void putMinion(int side, Minion minion){
        Log.d("Game", NAMES[turnSide] + "將" + minion + "放到場上");
        minions.get(side).add(minion);
        if(observer != null){
            observer.onMinionSummoned(side, minion);
        }
    }

    public interface Observer {
        void onDeckPrepared();
        void onFirstHandDetermined(int side);
        void onCoinGained(int side, Card coin);
        void onCardDraw(int side, Card card);
        void onCardBurn(int side, Card card);
        void onTired(int side, int damage);
        void onManaChanged(int side, int mana);
        void onMaxManaChanged(int side, int maxMana);
        void onInitialDrawFinished();
        void onCardUsed(int side, int i, Card c);
        void onMinionSummoned(int side, Minion minion);
        void onSideChanged(int side);
    }
}
