package com.example.vacineaqui.databaseLocal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vacineaqui.databaseNode.PostoDeVacina;

public class Delete extends SQLiteOpenHelper {
    private static final String NOME_DB = "BancoPosto";
    private static final int VERSAO_DB = 1;
    private static final String TABELA_POSTOVACINA = "PostoVacina";
    private static final String PATH_DB = "/data/user/0/com.example.vacineaqui/database/BancoPosto";
    private Context contexto;
    private SQLiteDatabase db;

    public Delete(Context context) {
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

    public boolean deleteTable(){
        openDB();
        String delete = "DROP TABLE IF EXISTS " + TABELA_POSTOVACINA;
        try{
            db.execSQL(delete);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.close();
        }
    }

    public boolean deletePostoVacina(PostoDeVacina p){
        openDB();
        String DPV = "ID = '"+p.getId()+"'";
        try{
            db.delete(TABELA_POSTOVACINA,DPV,null);
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
