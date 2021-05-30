package com.example.vacineaqui.databaseLocal;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.vacineaqui.databaseNode.PostoDeVacina;
import com.example.vacineaqui.databaseNode.RetrofitInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public boolean InserirNode(RetrofitInterface retrofitInterface, Context context){
        openDB();
        Call<List<PostoDeVacina>> call = retrofitInterface.executeFindAll();
        call.enqueue(new Callback<List<PostoDeVacina>>() {
            @Override
            public void onResponse(Call<List<PostoDeVacina>> call, Response<List<PostoDeVacina>> response) {
                if (response.code() == 200) {
                    List<PostoDeVacina> result = response.body();
                    for(int i = 0;i < result.size();i++){
                            ContentValues p = new ContentValues();
                            p.put("ID", result.get(i).getId());
                            p.put("SENHA", result.get(i).getSenha());
                            p.put("NOME", result.get(i).getNome());
                            p.put("LATITUDE", result.get(i).getLatitude());
                            p.put("LONGITUDE", result.get(i).getLongitude());
                            p.put("DISPONIBILIDADE", String.valueOf(result.get(i).getDisponibilidade()));
                            p.put("PACIENTES", result.get(i).getPacientes());
                            p.put("ENFERMEIROS", result.get(i).getEnfermeiros());
                            p.put("INFO", result.get(i).getInfo());
                            db.insert(TABELA_POSTOVACINA,null,p);
                    }
                } else if (response.code() == 404) {
                    Toast.makeText(context, "Dados não recebidos", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<PostoDeVacina>> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return true;
    }

    public boolean UpdateNode(RetrofitInterface retrofitInterface, Context context){
        openDB();
        Call<List<PostoDeVacina>> call = retrofitInterface.executeFindAll();
        call.enqueue(new Callback<List<PostoDeVacina>>() {
            @Override
            public void onResponse(Call<List<PostoDeVacina>> call, Response<List<PostoDeVacina>> response) {
                if (response.code() == 200) {
                    List<PostoDeVacina> result = response.body();
                    for(int i = 0;i < result.size();i++){
                        ContentValues p = new ContentValues();
                        p.put("ID", result.get(i).getId());
                        p.put("SENHA", result.get(i).getSenha());
                        p.put("NOME", result.get(i).getNome());
                        p.put("LATITUDE", result.get(i).getLatitude());
                        p.put("LONGITUDE", result.get(i).getLongitude());
                        p.put("DISPONIBILIDADE", String.valueOf(result.get(i).getDisponibilidade()));
                        p.put("PACIENTES", result.get(i).getPacientes());
                        p.put("ENFERMEIROS", result.get(i).getEnfermeiros());
                        p.put("INFO", result.get(i).getInfo());
                        System.out.println(result.get(i).getId()+" "+result.get(i).getNome());
                        String where = "ID = '"+result.get(i).getId()+"'";
                        db.update(TABELA_POSTOVACINA, p, where, null);
                    }
                } else if (response.code() == 404) {
                    Toast.makeText(context, "Dados não recebidos", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<PostoDeVacina>> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return true;
    }

    @SuppressLint("WrongConstant")
    private void openDB(){
        if(!db.isOpen()){
            db = contexto.openOrCreateDatabase(PATH_DB, SQLiteDatabase.OPEN_READWRITE,null);
        }
    }
}
