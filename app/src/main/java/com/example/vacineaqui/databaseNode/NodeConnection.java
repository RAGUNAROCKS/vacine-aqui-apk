package com.example.vacineaqui.databaseNode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.vacineaqui.Filometro;
import com.example.vacineaqui.MapsActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NodeConnection {

    public void login(RetrofitInterface retrofitInterface, HashMap<String, String> map, Context context){
        Call<PostoDeVacina> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<PostoDeVacina>() {
            @Override
            public void onResponse(Call<PostoDeVacina> call, Response<PostoDeVacina> response) {
                if(response.code() == 200) {
                    PostoDeVacina result = response.body();
                    Intent intent = new Intent(context, Filometro.class);
                    Bundle parametros = new Bundle();
                    parametros.putInt("ID", result.getId());
                    intent.putExtras(parametros);
                    context.startActivity(intent);
                }else if(response.code() == 404){
                    Toast.makeText(context, "Usuário Inválido", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PostoDeVacina> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void salvarFilometro(RetrofitInterface retrofitInterface, HashMap<String, String> map, Context context){
        Call<Void> callS = retrofitInterface.executeFilometro(map);
        callS.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200) {
                    Intent salvar = new Intent(context, MapsActivity.class);
                    context.startActivity(salvar);
                    Toast.makeText(context, "Registro Salvo", Toast.LENGTH_LONG).show();
                }else if(response.code() == 404){
                    Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void dispPosto(RetrofitInterface retrofitInterface, HashMap<String, String> map, Context context, boolean aberto){
        Call<Void> callS = retrofitInterface.executeFilometro(map);
        callS.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200) {
                    Intent salvar = new Intent(context, MapsActivity.class);
                    context.startActivity(salvar);
                    if(aberto) Toast.makeText(context, "Posto Aberto", Toast.LENGTH_LONG).show();
                    else Toast.makeText(context, "Posto Fechado", Toast.LENGTH_LONG).show();
                }else if(response.code() == 404){
                    Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}