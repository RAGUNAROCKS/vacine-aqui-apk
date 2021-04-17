package com.example.vacineaqui.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vacineaqui.PostoDeVacina;

public class Update extends SQLiteOpenHelper {
    private static final String NOME_DB = "BancoPosto";
    private static final int VERSAO_DB = 1;
    private static final String TABELA_POSTOVACINA = "PostoVacina";
    private static final String PATH_DB = "/data/user/0/com.example.vacineaqui/database/BancoPosto";
    private Context contexto;
    private SQLiteDatabase db;

    public Update(Context context) {
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

    public boolean inserir(PostoDeVacina p){
        openDB();
        try{
            ContentValues cv = new ContentValues();
            cv.put("ID", p.getId());
            cv.put("NOME", p.getNome());
            cv.put("LATITUDE", p.getLatitude());
            cv.put("LONGITUDE", p.getLongitude());
            cv.put("DISPONIBILIDADE", String.valueOf(p.getDisponibilidade()));
            db.insert(TABELA_POSTOVACINA,null,cv);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.close();
        }
    }

    public boolean update(PostoDeVacina p){
        openDB();
        try{
            String where = "ID = '"+p.getId()+"'";
            ContentValues cv = new ContentValues();
            cv.put("ID", p.getId());
            cv.put("NOME", p.getNome());
            cv.put("LATITUDE", p.getLatitude());
            cv.put("LONGITUDE", p.getLongitude());
            cv.put("DISPONIBILIDADE", String.valueOf(p.getDisponibilidade()));
            db.update(TABELA_POSTOVACINA, cv, where, null);
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
