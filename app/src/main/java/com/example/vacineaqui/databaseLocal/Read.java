package com.example.vacineaqui.databaseLocal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vacineaqui.databaseNode.PostoDeVacina;

import java.util.ArrayList;
import java.util.List;

public class Read extends SQLiteOpenHelper {
    private static final String NOME_DB = "BancoPosto";
    private static final int VERSAO_DB = 1;
    private static final String TABELA_POSTOVACINA = "PostoVacina";
    private static final String PATH_DB = "/data/user/0/com.example.vacineaqui/database/BancoPosto";
    private Context contexto;
    private SQLiteDatabase db;

    public Read(Context context) {
        super(context, NOME_DB, null, VERSAO_DB);
        this.contexto = context;
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<PostoDeVacina> buscarTodos(){
        List<PostoDeVacina> pArray = new ArrayList<>();
        openDB();
        String busca = "SELECT * FROM " + TABELA_POSTOVACINA;

        try{
            Cursor c = db.rawQuery(busca, null);
            if(c.moveToFirst()){
                do{
                    PostoDeVacina p = new PostoDeVacina();
                    p.setId(c.getInt(0));
                    p.setSenha(c.getString(1));
                    p.setNome(c.getString(2));
                    p.setLatitude(c.getDouble(3));
                    p.setLongitude(c.getDouble(4));
                    p.setDisponibilidade(Boolean.parseBoolean(c.getString(5)));
                    p.setPacientes(c.getInt(6));
                    p.setEnfermeiros(c.getInt(7));
                    pArray.add(p);
                }while(c.moveToNext());
                c.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            db.close();
        }
        return pArray;
    }

    @SuppressLint("WrongConstant")
    private void openDB(){
        if(!db.isOpen()){
            db = contexto.openOrCreateDatabase(PATH_DB, SQLiteDatabase.OPEN_READWRITE,null);
        }
    }


}
