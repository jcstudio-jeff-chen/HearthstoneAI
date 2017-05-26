package com.jcstudio.hearthstoneai;

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

    public static Card randomCard(int cost, double tauntRate){
        boolean hasTaunt = Math.random() < tauntRate;
        int totalValue = cost*2+2;
        Random r = new Random();
        int hp = r.nextInt(totalValue-1)+1;
        int atk = totalValue-hp-(hasTaunt ? 1:0);
        return new Card(cost, atk, hp, hasTaunt);
    }
}
