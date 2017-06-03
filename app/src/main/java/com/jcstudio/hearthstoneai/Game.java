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

    public int firstHand;
    public int turnSide;
    public int[] hp = {30, 30};
    public int[] mana = {0, 0};
    public int[] maxMana = {0, 0};
    public int[] tiredAmount = {0, 0};
    public int turn = 0;

    public ArrayList<Observer> observers;

    public Game(){
        for(int i = 0; i < 2; i++) {
            decks.add(new ArrayList<Card>(30));
            handCards.add(new ArrayList<Card>(10));
            minions.add(new ArrayList<Minion>(7));
        }

        observers = new ArrayList<>();
    }

    public void addObserver(Observer observer){
        observers.add(observer);
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
        for(Observer observer : observers){
            observer.onDeckPrepared();
        }
    }

    public void initialDraw(){
        Random r = new Random();
        turnSide = r.nextInt(2);
        Log.d("Game", NAMES[turnSide] + "先手");
        firstHand = turnSide;
        for(Observer observer : observers){
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
        for(Observer observer: observers){
            observer.onCoinGained(1-turnSide, coin);
            observer.onInitialDrawFinished();
        }
        turn++;
        setManaForNewTurn();
        drawCardForNewTurn();
        for(Observer observer: observers){
            observer.onSideChanged(turnSide);
        }
    }

    private void drawCard(int side){
        ArrayList<Card> deck = decks.get(side);
        ArrayList<Card> handCard = handCards.get(side);
        if(deck.isEmpty()){
            tiredAmount[side]++;
            Log.d("Game", NAMES[side] + "疲勞傷害 " + tiredAmount[side] + " 點");
            hp[side] -= tiredAmount[side];

            for(Observer observer:observers){
                observer.onTired(side, tiredAmount[side]);
                observer.onHeroDamaged(side, tiredAmount[side], hp[side]);
            }
            return;
        }
        Card c = deck.remove(0);
        if(handCard.size() == 10){
            Log.d("Game", NAMES[side] + "爆牌: " + c);
            for(Observer observer : observers){
                observer.onCardBurn(side, c);
            }
            return;
        }

        Log.d("Game", NAMES[side] + "抽牌: " + c);
        handCard.add(c);
        for(Observer observer : observers){
            observer.onCardDraw(side, c, turn == 0);
        }
    }

    public void changeSide(){
        Log.d("Game", NAMES[1-turnSide] + "回合");
        turnSide = 1-turnSide;
        turn++;
        weakUpMinions();
        setManaForNewTurn();
        drawCardForNewTurn();
        for(Observer observer : observers){
            observer.onSideChanged(turnSide);
        }
    }

    private void weakUpMinions(){
        for(Minion m : minions.get(turnSide)){
            m.isSleeping = false;
            m.isResting = false;
        }
    }

    private void setMaxMana(int side, int value){
        Log.d("Game", NAMES[side] + "水晶上限由 " + maxMana[side] + " 設為 " + value);
        if(value != maxMana[side]){
            maxMana[side] = value;
            for(Observer observer : observers){
                observer.onMaxManaChanged(side, value);
            }
        }
    }

    private void setMana(int side, int value){
        Log.d("Game", NAMES[side] + "水晶由 " + mana[side] + " 設為 " + value);
        if(value != mana[side]){
            mana[side] = value;
            for(Observer observer : observers){
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

    private void fillMana(int side){
        setMana(side, maxMana[side]);
    }

    private void setManaForNewTurn(){
        int newMaxMana = Math.min(maxMana[turnSide]+1, 10);
        setMaxMana(turnSide, newMaxMana);
        fillMana(turnSide);
    }

    private void drawCardForNewTurn(){
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

    public boolean isUsable(int side, int p) {
        return p < handCards.get(side).size() && isUsable(side, handCards.get(side).get(p));
    }

    public void useCard(int i){
        Card c = handCards.get(turnSide).remove(i);
        Log.d("Game", NAMES[turnSide] + "使用第 " + i + " 張手牌：" + c);
        for(Observer observer : observers){
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
        for(Observer observer : observers){
            observer.onMinionSummoned(side, minion);
        }
    }

    public boolean isMovable(int side, Minion minion){
        return side == turnSide && !minion.isSleeping && !minion.isResting && minion.atk > 0;
    }

    public boolean isMovable(int side, int p) {
        return p < minions.get(side).size() && isMovable(side, minions.get(side).get(p));
    }

    public boolean isMinionAttackable(int side, Minion m){
        if(side == turnSide){
            return false;
        }
        if(m.hasTaunt){
            return true;
        }
        for(Minion m2 : minions.get(side)){
            if(m2.hasTaunt){
                return false;
            }
        }
        return true;
    }

    public boolean isHeroAttackable(int side){
        if(side == turnSide){
            return false;
        }
        for(Minion m : minions.get(side)){
            if(m.hasTaunt){
                return false;
            }
        }
        return true;
    }

    public boolean isAttackable(int side, int p) {
        if (p == 7) {
            return isHeroAttackable(side);
        }
        return p < minions.get(side).size() && isMinionAttackable(side, minions.get(side).get(p));
    }

    public void attack(int side, int p1, int p2){
        int opponent = 1-side;
        Minion attacker = minions.get(side).get(p1);
        if(p2 == 7){
            Log.d("GAME", NAMES[side] + "使用" + attacker + "攻擊" + NAMES[opponent] + "的英雄");
            for(Observer observer : observers){
                observer.onAttack(side, p1, p2, attacker, null);
            }
            hp[opponent] -= attacker.atk;
            Log.d("Game", "onHeroDamaged, damage = " + attacker.atk + ", hp = " + hp[opponent]);
            for(Observer observer : observers){
                observer.onHeroDamaged(opponent, attacker.atk, hp[opponent]);
            }
            if(hp[opponent] <= 0){
                Log.d("GAME", NAMES[opponent] + "的英雄死亡");
                for(Observer observer : observers){
                    observer.onHeroDead(opponent);
                }
            }
        } else {
            Minion attacked = minions.get(opponent).get(p2);
            Log.d("GAME", NAMES[side] + "使用" + attacker + "攻擊" + NAMES[opponent] + "的" + attacked);
            for(Observer observer : observers){
                observer.onAttack(side, p1, p2, attacker, attacked);
            }
            attacked.currentHp -= attacker.atk;
            for(Observer observer : observers){
                observer.onMinionDamaged(opponent, p2, attacked, attacker.atk, attacked.currentHp);
            }
            if (attacked.atk > 0) {
                attacker.currentHp -= attacked.atk;
                for(Observer observer : observers){
                    observer.onMinionDamaged(side, p1, attacker, attacked.atk, attacker.currentHp);
                }
            }
            if (attacked.currentHp <= 0) {
                Log.d("GAME", NAMES[opponent] + "的" + attacked + "死亡");
                minions.get(opponent).remove(attacked);
                for(Observer observer : observers){
                    observer.onMinionDead(opponent, p2, attacked);
                }
            }
            if (attacker.currentHp <= 0) {
                Log.d("GAME", NAMES[side] + "的" + attacker + "死亡");
                minions.get(side).remove(attacker);
                for(Observer observer : observers){
                    observer.onMinionDead(side, p1, attacker);
                }
            }
        }
        if(attacker.currentHp > 0) {
            attacker.isResting = true;
            for(Observer observer : observers){
                observer.onMinionRested(side, p1, attacker);
            }
        }
    }

    public boolean isActionAvailable(int side, int code){
        if(code < 56){
            int p1 = code/8;
            int p2 = code%8;
            return isMovable(side, p1) && isAttackable(1-side, p2);
        }
        if(code < 66){
            int p = code-56;
            return isUsable(side, p);
        }
        return true;
    }

    public void performAction(int code){
        if(code < 56){
            int p1 = code/8;
            int p2 = code%8;
            attack(turnSide, p1, p2);
            return;
        }
        if(code < 66){
            int p = code-56;
            useCard(p);
            return;
        }
        changeSide();
    }

    public interface Observer {
        void onDeckPrepared();
        void onFirstHandDetermined(int side);
        void onCoinGained(int side, Card coin);
        void onCardDraw(int side, Card card, boolean isInitialDraw);
        void onCardBurn(int side, Card card);
        void onTired(int side, int damage);
        void onManaChanged(int side, int mana);
        void onMaxManaChanged(int side, int maxMana);
        void onInitialDrawFinished();
        void onCardUsed(int side, int i, Card c);
        void onMinionSummoned(int side, Minion minion);
        void onSideChanged(int side);
        void onHeroDamaged(int side, int damage, int hpAfterDamage);
        void onMinionDamaged(int side, int p, Minion m, int damage, int hpAfterDamage);
        void onHeroDead(int side);
        void onMinionDead(int side, int p, Minion m);
        void onMinionRested(int side, int p, Minion m);
        void onAttack(int side, int p1, int p2, Minion m1, Minion m2);
    }
}
