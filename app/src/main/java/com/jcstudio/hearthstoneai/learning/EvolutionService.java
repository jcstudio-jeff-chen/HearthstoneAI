package com.jcstudio.hearthstoneai.learning;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.jcstudio.hearthstoneai.LearnActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EvolutionService extends IntentService implements EvolutionAlgorithmRunner.EvolveCallback, EvolutionAlgorithmRunner.InitCallback {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String EVOLVE = "com.jcstudio.hearthstoneai.learning.action.EVOLVE";
    private static final String INITIALIZE = "com.jcstudio.hearthstoneai.learning.action.INITIALIZE";
    public static final String UPDATE_PROGRESS = "com.jcstudio.hearthstoneai.learning.action.UPDATE_PROGRESS";
    public static final String FINISH_GENERATION = "com.jcstudio.hearthstoneai.learning.action.FINISH_GENERATION";
    public static final String UPDATE_INIT_PROGRESS = "com.jcstudio.hearthstoneai.learning.action.UPDATE_INIT_PROGRESS";

    // TODO: Rename parameters
    private static final String GENERATION = "com.jcstudio.hearthstoneai.learning.extra.GENERATION";
    private static final String FIGHT_RATE = "com.jcstudio.hearthstoneai.learning.extra.FIGHT_RATE";

    public static final String JOB = "com.jcstudio.hearthstoneai.learning.extra.JOB";
    public static final String PROGRESS = "com.jcstudio.hearthstoneai.learning.extra.PROGRESS";
    public static final String TOTAL = "com.jcstudio.hearthstoneai.learning.extra.TOTAL";
    public static final String GEN_PROGRESS = "com.jcstudio.hearthstoneai.learning.extra.GEN_PROGRESS";
    public static final String GEN_TOTAL = "com.jcstudio.hearthstoneai.learning.extra.GEN_TOTAL";

    private EvolutionAlgorithmRunner runner;
    private GeneticAlgorithm ga;
    private HsEvaluator evaluator;
    private int genTotal;
    private int genProgress;

    public EvolutionService() {
        super("EvolutionService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HsNeuralNetwork nn = new HsNeuralNetwork();
        ga = new GeneticAlgorithm(0.05);
        evaluator = new HsEvaluator();
        runner = new EvolutionAlgorithmRunner(this, LearnActivity.POPULATION_SIZE, nn.nParam(), ga, evaluator);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startEvolve(Context context, int generation, double fightRate){
        Intent intent = new Intent(context, EvolutionService.class);
        intent.setAction(EVOLVE);
        intent.putExtra(GENERATION, generation);
        intent.putExtra(FIGHT_RATE, fightRate);
        context.startService(intent);
    }

    public static void startInitialize(Context context){
        Intent intent = new Intent(context, EvolutionService.class);
        intent.setAction(INITIALIZE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(action.equals(EVOLVE)){
                final int generation = intent.getIntExtra(GENERATION, 1);
                final double fightRate = intent.getDoubleExtra(FIGHT_RATE, 0.5);
                handleEvolve(generation, fightRate);
            } else if(action.equals(INITIALIZE)){
                handleInitialize();
            }
        }
    }

    private void handleEvolve(int generation, double fightRate){
        evaluator.fightRate = fightRate;
        genTotal = generation;
        genProgress = 0;
        Intent intent = new Intent(FINISH_GENERATION);
        intent.putExtra(PROGRESS, 0);
        intent.putExtra(TOTAL, generation);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        for(int i = 0; i < generation; i++){
            runner.evolve(this);
            genProgress = i+1;
            intent = new Intent(FINISH_GENERATION);
            intent.putExtra(PROGRESS, i+1);
            intent.putExtra(TOTAL, generation);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void handleInitialize(){
        runner.initialize(null, null, this);
    }

    @Override
    public void onProgressUpdate(String job, int progress, int total) {
        Intent intent = new Intent(UPDATE_PROGRESS);
        intent.putExtra(JOB, job);
        intent.putExtra(PROGRESS, progress);
        intent.putExtra(TOTAL, total);
        intent.putExtra(GEN_PROGRESS, genProgress);
        intent.putExtra(GEN_TOTAL, genTotal);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onEvolved(int generation, double time, EvolutionAlgorithmDatabase db) {
        // Will not call because not evolve async.
    }

    @Override
    public void onStart(int total) {
        // Do nothing
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        Intent intent = new Intent(UPDATE_INIT_PROGRESS);
        intent.putExtra(PROGRESS, progress);
        intent.putExtra(TOTAL, total);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onInitFinish(int total) {

    }
}
