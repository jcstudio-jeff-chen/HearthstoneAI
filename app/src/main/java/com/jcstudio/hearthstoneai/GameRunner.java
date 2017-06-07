package com.jcstudio.hearthstoneai;

import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by jeffrey on 2017/6/6.
 */

public class GameRunner implements Game.Observer{
    public AI ai[];
    public Game game;
    public BoardData boardData;

    public boolean paused = false;

    public GameRunner(AI up, AI down, Game.Observer observer){
        ai = new AI[]{up, down};
        game = new Game();
        boardData = new BoardData(game);
        game.addObserver(this);
        if(observer != null) {
            game.addObserver(observer);
        }
    }

    public void start(GameOverListener listener){
        game.resetMana();
        game.createDeck();
        game.shuffle();
        game.initialDraw();
        listener.gameOver(game.hp[0] > 0 ? 0 : 1);
    }

    public void doSomething(){
        int side = game.turnSide;
        ArrayList<Integer> actions = ai[side].getActionList(boardData.createArray(side));
        for(int action : actions){
            if(game.isActionAvailable(side, action)){
                game.performAction(action);
                if(action != Game.POSSIBLE_ACTIONS-1 && !game.isOver() && !paused){
                    doSomething();
                }
                break;
            }
        }
    }

    public void pause(){

    }

    public void resume(){

    }

    @Override
    public void onDeckPrepared() {
        // Do nothing
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

    }

    @Override
    public void onMinionSummoned(int side, Minion minion) {

    }

    @Override
    public void onSideChanged(int side) {
        doSomething();
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

    public interface GameOverListener{
        void gameOver(int winner);
    }
}
