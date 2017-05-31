package com.jcstudio.hearthstoneai;

/**
 * Created by jeffrey on 2017/5/31.
 */

public class Minion extends Card {
    int currentHp;
    public Minion(Card c){
        this.cost = c.cost;
        this.atk = c.atk;
        this.hp = c.hp;
        this.currentHp = this.hp;
        this.isCoin = false;
        this.hasTaunt = c.hasTaunt;
    }

    @Override
    public String shownName() {
        return cost + "\n" + atk + "/" + currentHp;
    }
}
