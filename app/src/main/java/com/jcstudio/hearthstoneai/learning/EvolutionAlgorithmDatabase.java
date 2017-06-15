package com.jcstudio.hearthstoneai.learning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

public class EvolutionAlgorithmDatabase {
    public SQLiteDatabase db=null; // 資料庫類別的物件
    private final static String	DATABASE_NAME= "gene_db.db";// 資料庫名稱
    private final static String	TABLE_NAME="gene_table"; // 資料表名稱
    private final static String GENERATION = "generation";
    private final static String POPULATION_ID = "population_id";
    private final static String POSITION = "position";
    private final static String VALUE = "value";
    public int dimension;
    // 資料表欄位
    private final static String	_ID	= "_id";;

    public EvolutionAlgorithmDatabase(Context context, int dimension){
        this.dimension = dimension;
        db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
        StringBuilder createTable = new StringBuilder();
        createTable.append("CREATE TABLE " + TABLE_NAME);
        createTable.append(" (" + _ID + " INTEGER PRIMARY KEY,");
        createTable.append(GENERATION + " INTEGER,");
        createTable.append(POPULATION_ID + " INTEGER,");
        createTable.append(POSITION + " INTEGER,");
        createTable.append(VALUE + " REAL)");
        try	{
            db.execSQL(createTable.toString());// 建立資料表
            db.execSQL("CREATE INDEX pop_id ON " + TABLE_NAME + " (" + POPULATION_ID + ");");
        }catch (Exception e) {
        }
    }

    public void close() {  // 關閉資料庫
        db.close();
    }

    public void add(double[] vector, int generation, int populationId){
        db.beginTransaction();
        for(int i = 0; i < vector.length; i++){
            ContentValues args = new ContentValues();
            args.put(GENERATION, generation);
            args.put(POPULATION_ID, populationId);
            args.put(POSITION, i);
            args.put(VALUE, vector[i]);
            db.insert(TABLE_NAME, null, args);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public double[] get(int populationId){
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{_ID, GENERATION, POPULATION_ID, POSITION, VALUE},
                POPULATION_ID + "=" + populationId,
                null, null, null, null, null);
        cursor.moveToFirst();
        double[] output = new double[dimension];
        for(int i = 0; i < dimension; i++){
            int index = cursor.getInt(3);
            double value = cursor.getDouble(4);
            output[index] = value;
            cursor.moveToNext();
        }
        cursor.close();
        return output;
    }

    public int deleteGeneration(int generation){
        return db.delete(TABLE_NAME, GENERATION + "=" + generation, null);
    }

    public int delete(int populationId) {
        return db.delete(TABLE_NAME, POPULATION_ID + "=" + populationId, null);
    }

    // 更新資料紀錄
    public void update(int populationId, int generation, double[] vector) {
        db.beginTransaction();
        for(int i = 0; i < vector.length; i++){
            ContentValues args = new ContentValues();
            args.put(GENERATION, generation);
            args.put(POPULATION_ID, populationId);
            args.put(POSITION, i);
            args.put(VALUE, vector[i]);
            db.update(TABLE_NAME, args, POPULATION_ID + "=" + populationId, null);
            Log.d("db", "update " + i);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public boolean deleteAll(){
        return db.delete(TABLE_NAME, null, null) > 0;
    }
}
