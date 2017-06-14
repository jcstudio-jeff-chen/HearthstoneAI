package com.jcstudio.hearthstoneai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmDatabase;
import com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmRunner;
import com.jcstudio.hearthstoneai.learning.EvolutionService;
import com.jcstudio.hearthstoneai.learning.GeneticAlgorithm;
import com.jcstudio.hearthstoneai.learning.HsEvaluator;
import com.jcstudio.hearthstoneai.learning.HsNeuralNetwork;

import java.util.Locale;

import static com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmRunner.GENERATION;
import static com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmRunner.MAX_FITNESS;

public class LearnActivity extends AppCompatActivity {
    public static final int POPULATION_SIZE = 50;
    private TextView tvGen;
    private TextView tvPupSize;
    private EditText etGen;
    private ProgressBar pbEvolve;
    private ProgressBar pbPub;
    private ProgressBar pbInit;
    private TextView tvGenProgress;
    private TextView tvGenTotal;
    private TextView tvPubProgress;
    private TextView tvPubTotal;
    private TextView tvInitProgress;
    private TextView tvInitTotal;
    private TextView tvDimension;
    private TextView tvWinRate;
    private TextView tvJob;
    private EditText etFightRate;
    /*private EvolutionAlgorithmRunner runner;
    private LocalStorage sp;
    private HsEvaluator evaluator;*/

    private ProgressUpdateReceiver receiver;
    private LocalStorage sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        sp = new LocalStorage(this);

        tvGen = (TextView) findViewById(R.id.tv_gen);
        tvPupSize = (TextView) findViewById(R.id.tv_pup_size);
        etGen = (EditText) findViewById(R.id.et_gen);
        pbEvolve = (ProgressBar) findViewById(R.id.pb_evolve);
        pbPub = (ProgressBar) findViewById(R.id.pb_pub);
        pbInit = (ProgressBar) findViewById(R.id.pb_init);
        tvGenProgress = (TextView) findViewById(R.id.tv_gen_progress);
        tvGenTotal = (TextView) findViewById(R.id.tv_gen_total);
        tvPubProgress = (TextView) findViewById(R.id.tv_pub_progress);
        tvPubTotal = (TextView) findViewById(R.id.tv_pub_total);
        tvInitProgress = (TextView) findViewById(R.id.tv_init_progress);
        tvInitTotal = (TextView) findViewById(R.id.tv_init_total);
        tvDimension = (TextView) findViewById(R.id.tv_dimension);
        tvWinRate = (TextView) findViewById(R.id.tv_win_rate);
        tvJob = (TextView) findViewById(R.id.tv_job);
        etFightRate = (EditText) findViewById(R.id.et_fight_rate);

        tvGen.setText(String.valueOf(sp.getInt(GENERATION)));
        tvPupSize.setText(String.valueOf(POPULATION_SIZE));
        HsNeuralNetwork nn = new HsNeuralNetwork();
        tvDimension.setText(String.valueOf(nn.nParam()));

        String s = String.format(Locale.getDefault(), "%.3f", sp.getFloat(MAX_FITNESS)*100) + "%";
        tvWinRate.setText(s);
        int fightRate = sp.getInt("fight_rate");
        if(fightRate <= 0){
            fightRate = 50;
        }
        etFightRate.setText(String.valueOf(fightRate));

        IntentFilter intentFilter = new IntentFilter(EvolutionService.UPDATE_PROGRESS);
        intentFilter.addAction(EvolutionService.FINISH_GENERATION);
        intentFilter.addAction(EvolutionService.UPDATE_INIT_PROGRESS);

        receiver = new ProgressUpdateReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    public void evolve(View v){
        int total = 1;
        try{
            total = Integer.valueOf(etGen.getText().toString());
        } catch (NumberFormatException ignored){

        }

        int fightRate = 50;
        try{
            fightRate = Integer.valueOf(etFightRate.getText().toString());
        } catch (NumberFormatException ignored){

        }

        sp.saveInt("fight_rate", fightRate);

        pbEvolve.setMax(total);
        pbEvolve.setProgress(0);
        tvGenTotal.setText(String.valueOf(total));
        tvGenProgress.setText("0");

        EvolutionService.startEvolve(this, total, fightRate/100.0);
    }

    /*

    private void _evolve(){
        runner.evolveAsync(new EvolutionAlgorithmRunner.EvolveCallback() {
            @Override
            public void onProgressUpdate(final String job, final int progress, final int total) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbPub.setMax(total);
                        pbPub.setProgress(progress);
                        tvPubProgress.setText(String.valueOf(progress));
                        tvPubTotal.setText(String.valueOf(total));
                        tvJob.setText(job);
                    }
                });
            }

            @Override
            public void onEvolved(int generation, double time, EvolutionAlgorithmDatabase db) {
                tvGen.setText(String.valueOf(generation));
                String s = String.format(Locale.getDefault(), "%.3f", sp.getFloat(MAX_FITNESS)*100) + "%";
                tvWinRate.setText(s);
                count++;
                pbEvolve.setProgress(count);
                tvGenProgress.setText(String.valueOf(count));
                if(count < total){
                    _evolve();
                } else {
                    pbPub.setProgress(0);
                    tvPubProgress.setText("0");
                    tvPubTotal.setText("0");
                    pbEvolve.setProgress(0);
                    tvGenProgress.setText("0");
                    tvGenTotal.setText("0");
                }
            }
        });
    }
    */

    public void reset(View v){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("AI 重置後會失去已學會的所有技巧，從頭開始學習，此動作無法復原，確定重置？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetAI();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void resetAI(){
        tvInitProgress.setText("0");
        tvInitTotal.setText(String.valueOf(POPULATION_SIZE));
        pbInit.setMax(POPULATION_SIZE);
        pbInit.setProgress(0);

        EvolutionService.startInitialize(this);

        /*runner.initialize(null, null, new EvolutionAlgorithmRunner.InitCallback() {
            @Override
            public void onStart(int total) {
                tvInitProgress.setText("0");
                tvInitTotal.setText(String.valueOf(total));
                pbInit.setMax(total);
                pbInit.setProgress(0);
            }

            @Override
            public void onProgressUpdate(int progress, int total) {
                tvInitProgress.setText(String.valueOf(progress));
                pbInit.setProgress(progress);
            }

            @Override
            public void onInitFinish(int total) {

            }
        });*/
    }


    private class ProgressUpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case EvolutionService.UPDATE_PROGRESS:
                    String job = intent.getStringExtra(EvolutionService.JOB);
                    int total = intent.getIntExtra(EvolutionService.TOTAL, 0);
                    int progress = intent.getIntExtra(EvolutionService.PROGRESS, 0);
                    int genTotal = intent.getIntExtra(EvolutionService.GEN_TOTAL, 0);
                    int genProgress = intent.getIntExtra(EvolutionService.GEN_PROGRESS, 0);
                    pbPub.setMax(total);
                    pbPub.setProgress(progress);
                    tvPubProgress.setText(String.valueOf(progress));
                    tvPubTotal.setText(String.valueOf(total));
                    tvJob.setText(job);
                    pbEvolve.setProgress(genProgress);
                    pbEvolve.setMax(genTotal);
                    tvGenProgress.setText(String.valueOf(genProgress));
                    tvGenTotal.setText(String.valueOf(genTotal));
                    break;
                case EvolutionService.FINISH_GENERATION:
                    int generation = sp.getInt(GENERATION);
                    total = intent.getIntExtra(EvolutionService.TOTAL, 0);
                    progress = intent.getIntExtra(EvolutionService.PROGRESS, 0);
                    tvGen.setText(String.valueOf(generation));
                    String s = String.format(Locale.getDefault(), "%.3f", sp.getFloat(MAX_FITNESS)*100) + "%";
                    tvWinRate.setText(s);
                    pbEvolve.setProgress(progress);
                    tvGenProgress.setText(String.valueOf(progress));
                    tvGenTotal.setText(String.valueOf(total));
                    if(total == progress){
                        pbPub.setProgress(0);
                        tvPubProgress.setText("0");
                        tvPubTotal.setText("0");
                        pbEvolve.setMax(total);
                        pbEvolve.setProgress(0);
                        tvGenProgress.setText("0");
                        tvGenTotal.setText("0");
                    }
                    break;
                case EvolutionService.UPDATE_INIT_PROGRESS:
                    progress = intent.getIntExtra(EvolutionService.PROGRESS, 0);
                    tvInitProgress.setText(String.valueOf(progress));
                    pbInit.setProgress(progress);

                    if(progress == POPULATION_SIZE){
                        tvInitProgress.setText("0");
                        tvInitTotal.setText(String.valueOf(POPULATION_SIZE));
                        pbInit.setMax(POPULATION_SIZE);
                        pbInit.setProgress(0);
                        generation = sp.getInt(GENERATION);
                        tvGen.setText(String.valueOf(generation));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
