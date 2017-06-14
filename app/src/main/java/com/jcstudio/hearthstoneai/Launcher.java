package com.jcstudio.hearthstoneai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmRunner;
import com.jcstudio.hearthstoneai.learning.GeneticAlgorithm;
import com.jcstudio.hearthstoneai.learning.HsEvaluator;
import com.jcstudio.hearthstoneai.learning.HsNeuralNetwork;

import static com.jcstudio.hearthstoneai.LearnActivity.POPULATION_SIZE;

public class Launcher extends AppCompatActivity implements EvolutionAlgorithmRunner.InitCallback{

    private TextView tvProgress;
    private TextView tvTotal;
    private ProgressBar pbInit;
    private EvolutionAlgorithmRunner runner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        pbInit = (ProgressBar) findViewById(R.id.pb_init);

        HsNeuralNetwork nn = new HsNeuralNetwork();
        runner = new EvolutionAlgorithmRunner(this, POPULATION_SIZE, nn.nParam(), new GeneticAlgorithm(0.05), new HsEvaluator());
        boolean initialized = runner.initIfNotExist(null, null, this);
        if(initialized){
            goToMainPage();
        }
    }

    private void goToMainPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart(int total) {
        pbInit.setProgress(0);
        pbInit.setMax(total);
        tvTotal.setText(String.valueOf(total));
        tvProgress.setText("0");
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        pbInit.setProgress(progress);
        tvProgress.setText(String.valueOf(progress));
    }

    @Override
    public void onInitFinish(int total) {
        goToMainPage();
    }
}
