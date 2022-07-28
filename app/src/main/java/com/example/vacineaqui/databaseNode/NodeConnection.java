package com.example.vacineaqui.databaseNode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.vacineaqui.Filometro;
import com.example.vacineaqui.MapsActivity;
import com.example.vacineaqui.databaseLocal.Update;

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
                    parametros.putString("NOME", result.getNome());
                    parametros.putBoolean("DISPONIBILIDADE", result.getDisponibilidade());
                    parametros.putInt("PACIENTES", result.getPacientes());
                    parametros.putInt("ENFERMEIROS", result.getEnfermeiros());
                    intent.putExtras(parametros);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        Call<Void> call = retrofitInterface.executeFilometro(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200) {
                    Update u = new Update(context);
                    u.UpdateNode(retrofitInterface, context);
                    Intent salvar = new Intent(context, MapsActivity.class);
                    salvar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(salvar);
                    Toast.makeText(context, "Registro Salvo", Toast.LENGTH_LONG).show();
                    Filometro.connected = true;
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
        Call<Void> call = retrofitInterface.executeFilometro(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200) {
                    Update u = new Update(context);
                    u.UpdateNode(retrofitInterface, context);
                    Intent salvar = new Intent(context, MapsActivity.class);
                    salvar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(salvar);
                    if(aberto) Toast.makeText(context, "Posto Aberto", Toast.LENGTH_LONG).show();
                    else Toast.makeText(context, "Posto Fechado", Toast.LENGTH_LONG).show();
                    Filometro.connected = true;
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