package com.jcstudio.hearthstoneai;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by jeffrey on 2017/5/26.
 */

public class Card {
    int cost;
    int atk;
    int hp;
    boolean hasTaunt;
    boolean isCoin;

    public Card(int cost, int atk, int hp, boolean hasTaunt) {
        this.cost = cost;
        this.atk = atk;
        this.hp = hp;
        this.hasTaunt = hasTaunt;
        this.isCoin = false;
    }

    public Card(){
        isCoin = true;
    }

    @Override
    public String toString(){
        if(isCoin){
            return "幸運幣";
        }
        return cost + "/" + atk + "/" + hp + (hasTaunt ? " 嘲諷":"");
    }

    public String shownName(){
        if(isCoin){
            return "幣";
        }
        return cost + "\n" + atk + "/" + hp;
    }

    public int filterColor(){
        if(isCoin){
            return Color.CYAN;
        }
        return hasTaunt ? Color.GREEN : Color.TRANSPARENT;
    }

    public static Card randomCard(int cost, double tauntRate){
        boolean hasTaunt = Math.random() < tauntRate;
        int totalValue = (int) Math.round((cost*2+(hasTaunt ? 1:2))*(hasTaunt ? 0.9 : 1));
        int hp;
        int atk;
        double range = totalValue - (hasTaunt ? 1:2);
        double sum = 0;
        for(int i = 0; i < 3; i++){
            sum+=Math.random();
        }
        double a = sum/3;
        if(hasTaunt){
            a *= a;
        }

        atk = (int) Math.round(a*(range+1)-0.5) + (hasTaunt ? 0:1);
        hp = totalValue-atk;

        return new Card(cost, atk, hp, hasTaunt);
    }
}
