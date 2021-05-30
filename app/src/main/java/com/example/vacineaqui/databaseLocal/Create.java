package com.example.vacineaqui.databaseLocal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Create extends SQLiteOpenHelper {
    private static final String NOME_DB = "BancoPosto";
    private static final int VERSAO_DB = 1;
    private static final String TABELA_POSTOVACINA = "PostoVacina";
    private static final String PATH_DB = "/data/user/0/com.example.vacineaqui/database/BancoPosto";
    private Context contexto;
    private SQLiteDatabase db;

    public Create(Context context) {
        super(context, NOME_DB, null, VERSAO_DB);
        this.contexto = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean createTable(){
        openDB();
        String createTable = "CREATE TABLE IF NOT EXISTS "+TABELA_POSTOVACINA+" (" +
                "ID INTEGER PRIMARY KEY," +
                "SENHA TEXT," +
                "NOME TEXT," +
                "LATITUDE DOUBLE  NOT NULL," +
                "LONGITUDE DOUBLE  NOT NULL," +
                "DISPONIBILIDADE TEXT NOT NULL," +
                "PACIENTES INTEGER NOT NULL," +
                "ENFERMEIROS INTEGER NOT NULL," +
                "INFO TEXT)";
        try{
            db.execSQL(createTable);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.close();
        }
    }

    @SuppressLint("WrongConstant")
    private void openDB(){
        if(!db.isOpen()){
            db = contexto.openOrCreateDatabase(PATH_DB, SQLiteDatabase.OPEN_READWRITE,null);
        }
    }
}
