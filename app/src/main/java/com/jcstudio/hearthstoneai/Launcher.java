package com.jcstudio.hearthstoneai;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmRunner;
import com.jcstudio.hearthstoneai.learning.EvolveNnExpander;
import com.jcstudio.hearthstoneai.learning.GeneticAlgorithm;
import com.jcstudio.hearthstoneai.learning.HsEvaluator;
import com.jcstudio.hearthstoneai.learning.HsNeuralNetwork;
import com.jcstudio.hearthstoneai.learning.HsNeuralNetworkV0;

import static com.jcstudio.hearthstoneai.LearnActivity.POPULATION_SIZE;
import static com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmRunner.INITIALIZED;

public class Launcher extends AppCompatActivity implements EvolutionAlgorithmRunner.InitCallback{
    public static final int CURRENT_NN_VERSION = 1;
    public static final String NN_VERSION = "nn_version";

    private TextView tvJob;
    private TextView tvProgress;
    private TextView tvTotal;
    private ProgressBar pbInit;
    private LocalStorage sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        sp = new LocalStorage(this);

        tvJob = (TextView) findViewById(R.id.tv_job);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        pbInit = (ProgressBar) findViewById(R.id.pb_init);

        HsNeuralNetwork nn = new HsNeuralNetwork();

        boolean initialized = sp.getBoolean(INITIALIZED, false);
        if(!initialized){
            EvolutionAlgorithmRunner runner = new EvolutionAlgorithmRunner(this, POPULATION_SIZE, nn.nParam(), new GeneticAlgorithm(0.05), new HsEvaluator());
            runner.initialize(null, null, this);
        } else {
            int nnVersion = sp.getInt(NN_VERSION);
            if(nnVersion == CURRENT_NN_VERSION){
                goToMainPage();
            } else {
                if(nnVersion == 0){
                    HsNeuralNetworkV0 oldNn = new HsNeuralNetworkV0();
                    EvolveNnExpander expander = new EvolveNnExpander(this, oldNn, nn, POPULATION_SIZE);
                    tvJob.setText("擴充類神經網路");
                    expander.expandAsync(null, null, new EvolveNnExpander.ProgressListener() {
                        @Override
                        public void onProgressUpdate(final int progress, final int total) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pbInit.setMax(total);
                                    pbInit.setProgress(progress);
                                    tvProgress.setText(String.valueOf(progress));
                                    tvTotal.setText(String.valueOf(total));
                                    if(progress == total){
                                        sp.saveInt(NN_VERSION, CURRENT_NN_VERSION);
                                        goToMainPage();
                                    }
                                }
                            });
                        }
                    });
                }
            }
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
