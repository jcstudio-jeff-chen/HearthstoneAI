package com.jcstudio.hearthstoneai;

/**
 * Created by jeffrey on 2017/5/31.
 */

public class Minion extends Card {
    int currentHp;
    boolean isSleeping;
    boolean isResting;
    public Minion(Card c){
        this.cost = c.cost;
        this.atk = c.atk;
        this.hp = c.hp;
        this.currentHp = this.hp;
        this.isCoin = false;
        this.hasTaunt = c.hasTaunt;
        this.isSleeping = true;
        this.isResting = false;
    }

    @Override
    public  String toString() {
        if(isCoin){
            return "幸運幣";
        }
        return cost + "/" + atk + "/" + hp + "/殘血" + currentHp + (hasTaunt ? " 嘲諷":"") ;
    }

    @Override
    public String shownName() {
        return cost + "\n" + atk + "/" + currentHp;
    }
}
