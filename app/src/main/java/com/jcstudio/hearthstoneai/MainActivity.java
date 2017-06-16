package com.jcstudio.hearthstoneai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmDatabase;
import com.jcstudio.hearthstoneai.learning.HsNeuralNetwork;

import java.util.Random;

import static com.jcstudio.hearthstoneai.LearnActivity.POPULATION_SIZE;

public class MainActivity extends AppCompatActivity {

    EvolutionAlgorithmDatabase db;
    LocalStorage sp;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HsNeuralNetwork nn = new HsNeuralNetwork();
        nn.printNodeCount();
        db = new EvolutionAlgorithmDatabase(this, nn.nParam());
        sp = new LocalStorage(this);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("AI 載入中");
    }

    public void playWithAI(View v){
        final int generation = sp.getInt("generation");

        Random r = new Random();
        final int sn0 = r.nextInt(POPULATION_SIZE);

        pd.show();
        new AsyncTask<Void, Void, double[]>(){
            @Override
            protected double[] doInBackground(Void... params) {
                double[] param0 = db.get(sn0);
                return param0;
            }

            @Override
            protected void onPostExecute(double[] doubles) {
                super.onPostExecute(doubles);
                pd.dismiss();
                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                intent.putExtra("ai_param_0", doubles);
                intent.putExtra("generation", generation);
                intent.putExtra("sn_0", sn0);
                startActivity(intent);
            }
        }.execute();
    }

    public void aiFight(View v){
        final int generation = sp.getInt("generation");

        Random r = new Random();
        final int sn0 = r.nextInt(POPULATION_SIZE);
        int sn1_ = r.nextInt(POPULATION_SIZE-1);
        final int sn1 = sn1_ >= sn0 ? sn1_+1 : sn1_;

        pd.show();

        new AsyncTask<Void, Void, double[][]>(){

            @Override
            protected double[][] doInBackground(Void... params) {
                double[][] param = new double[2][];
                param[0] = db.get(sn0);
                param[1] = db.get(sn1);
                return param;
            }

            @Override
            protected void onPostExecute(double[][] doubles) {
                super.onPostExecute(doubles);
                pd.dismiss();
                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                intent.putExtra("ai_param_0", doubles[0]);
                intent.putExtra("ai_param_1", doubles[1]);
                intent.putExtra("generation", generation);
                intent.putExtra("sn_0", sn0);
                intent.putExtra("sn_1", sn1);
                //intent.putExtra("generation_0", );
                startActivity(intent);
            }
        }.execute();

    }

    public void evolveAI(View v){
        Intent intent = new Intent(this, LearnActivity.class);
        startActivity(intent);
    }

    public void testAI(View v){
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    public void showGameGuide(View v){
        new AlertDialog.Builder(this).setTitle(R.string.game_guide).setMessage(R.string.game_guide_content).show();
    }

    public void upload(View v){
        new AsyncTask<Void, Integer, double[]>(){
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected double[] doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(double[] doubles) {
                super.onPostExecute(doubles);
            }
        }.execute();
    }
}
