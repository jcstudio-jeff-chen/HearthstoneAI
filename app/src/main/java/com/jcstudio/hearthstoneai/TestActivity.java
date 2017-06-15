package com.jcstudio.hearthstoneai;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcstudio.hearthstoneai.learning.EvolutionAlgorithmDatabase;
import com.jcstudio.hearthstoneai.learning.HsNeuralNetwork;

import java.util.Locale;
import java.util.Random;

import static com.jcstudio.hearthstoneai.LearnActivity.POPULATION_SIZE;

public class TestActivity extends AppCompatActivity {
    public static final int FIGHTS = 100;

    private EvolutionAlgorithmDatabase db;
    private ProgressBar pbFight;
    private TextView tvJob;
    private TextView tvFightProgress;
    private TextView tvFightTotal;
    private TextView tvReportTotal;
    private TextView tvReportWin;
    private TextView tvReportLose;
    private TextView tvReportRate;
    private EditText etFights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        pbFight = (ProgressBar) findViewById(R.id.pb_fight);
        tvJob = (TextView) findViewById(R.id.tv_job);
        tvFightProgress = (TextView) findViewById(R.id.tv_fight_progress);
        tvFightTotal = (TextView) findViewById(R.id.tv_fight_total);
        tvReportTotal = (TextView) findViewById(R.id.tv_report_total);
        tvReportWin = (TextView) findViewById(R.id.tv_report_win);
        tvReportLose = (TextView) findViewById(R.id.tv_report_lose);
        tvReportRate = (TextView) findViewById(R.id.tv_report_rate);
        etFights = (EditText) findViewById(R.id.et_fights);

        HsNeuralNetwork nn = new HsNeuralNetwork();
        db = new EvolutionAlgorithmDatabase(this, nn.nParam());
    }

    public void startTest(View view){
        int x = FIGHTS;
        try{
            x = Integer.valueOf(etFights.getText().toString());
        } catch (NumberFormatException ignored){

        }

        final int totalFights = x;

        pbFight.setProgress(0);
        pbFight.setMax(totalFights);
        tvJob.setText("準備中");
        tvFightProgress.setText("0");
        tvFightTotal.setText(String.valueOf(totalFights));
        tvReportTotal.setText("0");
        tvReportWin.setText("0");
        tvReportLose.setText("0");
        tvReportRate.setText("0");
        new AsyncTask<Void, Integer, Integer>() {
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                int stage = values[0];
                int progress = values[1];
                if(stage == 0){
                    pbFight.setMax(POPULATION_SIZE);
                    tvFightTotal.setText(String.valueOf(POPULATION_SIZE));
                    tvJob.setText("載入參數");
                }
                if(stage == 1){
                    pbFight.setMax(totalFights);
                    tvFightTotal.setText(String.valueOf(totalFights));
                    int wins = values[2];
                    float rate = (float) wins/progress;
                    tvJob.setText("對戰");
                    tvReportTotal.setText(String.valueOf(progress));
                    tvReportWin.setText(String.valueOf(wins));
                    tvReportLose.setText(String.valueOf(progress-wins));
                    tvReportRate.setText(String.format(Locale.getDefault(), "%.3f", rate*100));
                }
                pbFight.setProgress(progress);
                tvFightProgress.setText(String.valueOf(progress));
            }

            @Override
            protected Integer doInBackground(Void... aVoid) {
                double[][] params = new double[POPULATION_SIZE][];
                publishProgress(0, 0);
                for(int i = 0; i < POPULATION_SIZE; i++){
                    params[i] = db.get(i);
                    publishProgress(0, i+1);
                }

                Random r = new Random();
                int wins = 0;
                int fights = 0;

                publishProgress(1, 0, 0);
                for(int i = 0; i < totalFights; i++){
                    int n = r.nextInt(POPULATION_SIZE);
                    double[] x = new double[db.dimension];
                    for(int j = 0; j < x.length; j++){
                        x[i] = Math.random()*2-1;
                    }
                    AI smartAI = new AI(params[n]);
                    AI stupidAI = new AI(x);
                    GameRunner runner = new GameRunner(smartAI, stupidAI, null);
                    int winner = runner.start(null);
                    if(winner == 0){
                        wins++;
                    }
                    fights++;
                    publishProgress(1, fights, wins);
                }
                return wins;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                tvJob.setText("完成");
                pbFight.setProgress(0);
                tvFightTotal.setText("0");
                tvFightProgress.setText("0");
            }
        }.execute();
    }
}
