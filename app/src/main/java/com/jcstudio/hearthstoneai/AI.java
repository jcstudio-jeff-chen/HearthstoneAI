package com.jcstudio.hearthstoneai;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by jeffrey on 2017/6/2.
 */

public class AI {
    public ArrayList<Integer> getActionList(double[] dataArray){
        for(int i = 0; i < dataArray.length; i++){
            Log.d("AI", "盤面參數[" + i + "] = " + dataArray[i]);
        }
        ArrayList<Integer> actions = new ArrayList<>(67);
        for(int i = 0; i < 67; i++){
            actions.add(i);
        }
        Collections.shuffle(actions);
        return actions;
    }
}
