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

    private int turnSide;
    private int[] hp = {30, 30};
    private int[] manna = {0, 0};
    private int[] maxManna = {0, 0};
    private int[] tiredAmount = {0, 0};

    public Observer observer;

    public Game(){
        for(int i = 0; i < 2; i++) {
            decks.add(new ArrayList<Card>(30));
            handCards.add(new ArrayList<Card>(10));
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
        handCards.get(1-turnSide).add(new Card());
        if(observer != null){
            observer.onCoinGained(1-turnSide);
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

    public interface Observer {
        void onDeckPrepared();
        void onFirstHandDetermined(int side);
        void onCoinGained(int side);
        void onCardDraw(int side, Card card);
        void onCardBurn(int side, Card card);
        void onTired(int side, int damage);
        void onTurn(int newSide);
    }
}
