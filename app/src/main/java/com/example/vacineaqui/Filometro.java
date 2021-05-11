package com.example.vacineaqui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacineaqui.databaseLocal.Update;
import com.example.vacineaqui.databaseNode.NodeConnection;
import com.example.vacineaqui.databaseNode.PostoDeVacina;
import com.example.vacineaqui.databaseNode.RetrofitInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Filometro extends Activity implements View.OnClickListener {
    Button btnAddPacientes, btnSubPacientes, btnAddEnfermeiros, btnSubEnfermeiros, btnSalvar, btnFecharPosto, btnSair;
    TextView lblQtdPacientes, lblQtdEnfermeiros, postoText, pacientesText, enfermeirosText, disponibilidadeText;
    private Retrofit retrofit; private RetrofitInterface retrofitInterface; private String BASE_URL = "http://10.0.2.2:3000";
    private int QTDPACIENTES = 0, QTDENFERMEIROS = 0, ID; PostoDeVacina result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filometro_dialog);
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).
                build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        modPacientes();
        modEnfermeiros();
        modAcoes();

        Intent filometro = getIntent();
        Bundle parametros = filometro.getExtras();

        if(parametros != null) {
            ID = parametros.getInt("ID");
            HashMap<String, String> map = new HashMap<>();
            map.put("id", Integer.toString(ID));
            Call<PostoDeVacina> call = retrofitInterface.executeFind(map);
            call.enqueue(new Callback<PostoDeVacina>() {
                @Override
                public void onResponse(Call<PostoDeVacina> call, Response<PostoDeVacina> response) {
                    if(response.code() == 200) {
                        result = response.body();
                        mostrarDados();
                    }else if(response.code() == 404){
                        Toast.makeText(Filometro.this, "Usuário não encontrado", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<PostoDeVacina> call, Throwable t) {
                    Toast.makeText(Filometro.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String vacinação(boolean d){ if(d) return "Sim"; else return "Não";}

    private void mostrarDados(){
        postoText = findViewById(R.id.postoText);
        pacientesText = findViewById(R.id.pacientesText);
        enfermeirosText = findViewById(R.id.enfermeirosText);
        disponibilidadeText = findViewById(R.id.disponibilidadeText);

        postoText.setText(result.getNome());
        pacientesText.setText(Integer.toString(result.getPacientes()));
        enfermeirosText.setText(Integer.toString(result.getEnfermeiros()));
        disponibilidadeText.setText(vacinação(result.getDisponibilidade()));
    }

    private void modPacientes(){
        lblQtdPacientes = findViewById(R.id.lblQtdPacientes);
        btnAddPacientes = findViewById(R.id.btnAddPacientes);
        btnSubPacientes = findViewById(R.id.btnSubPacientes);

        lblQtdPacientes.setText(Integer.toString(QTDPACIENTES));
        btnAddPacientes.setOnClickListener(this);
        btnSubPacientes.setOnClickListener(this);
    }

    private void modEnfermeiros(){
        lblQtdEnfermeiros = findViewById(R.id.lblQtdEnfermeiros);
        btnAddEnfermeiros = findViewById(R.id.btnAddEnfermeiros);
        btnSubEnfermeiros = findViewById(R.id.btnSubEnfermeiros);

        lblQtdEnfermeiros.setText(Integer.toString(QTDENFERMEIROS));
        btnAddEnfermeiros.setOnClickListener(this);
        btnSubEnfermeiros.setOnClickListener(this);
    }

    private void modAcoes(){
        btnSalvar = findViewById(R.id.btnSalvar);
        btnFecharPosto = findViewById(R.id.btnFecharPosto);
        btnSair = findViewById(R.id.btnSair);

        btnSalvar.setOnClickListener(this);
        btnFecharPosto.setOnClickListener(this);
        btnSair.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Update u = new Update(getApplicationContext());
        switch (v.getId()){
            case R.id.btnAddPacientes:
                if(result.getDisponibilidade()) QTDPACIENTES++;
                lblQtdPacientes.setText(Integer.toString(QTDPACIENTES));
                break;
            case R.id.btnSubPacientes:
                if(result.getDisponibilidade()) QTDPACIENTES--;
                lblQtdPacientes.setText(Integer.toString(QTDPACIENTES));
                break;
            case R.id.btnAddEnfermeiros:
                if(result.getDisponibilidade()) QTDENFERMEIROS++;
                lblQtdEnfermeiros.setText(Integer.toString(QTDENFERMEIROS));
                break;
            case R.id.btnSubEnfermeiros:
                if(result.getDisponibilidade()) QTDENFERMEIROS--;
                lblQtdEnfermeiros.setText(Integer.toString(QTDENFERMEIROS));
                break;
            case R.id.btnSalvar:
                NodeConnection nodeSalvar = new NodeConnection();
                if(result.getPacientes() + QTDPACIENTES >= 0 && result.getEnfermeiros() + QTDENFERMEIROS > 0){
                    HashMap<String, String> salvar = new HashMap<>();
                    salvar.put("id", Integer.toString(ID));
                    salvar.put("disponibilidade", String.valueOf(result.getDisponibilidade()));
                    salvar.put("pacientes", Integer.toString((result.getPacientes() + QTDPACIENTES)));
                    salvar.put("enfermeiros", Integer.toString((result.getEnfermeiros() + QTDENFERMEIROS)));
                    nodeSalvar.salvarFilometro(retrofitInterface, salvar, getApplicationContext());
                }else{
                    Toast.makeText(getApplicationContext(), "Redução ultrapassa a quantidade de pessoas", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnFecharPosto:
                NodeConnection nodeSitPosto = new NodeConnection();
                if(result.getDisponibilidade() == true){
                    HashMap<String, String> estPosto = new HashMap<>();
                    estPosto.put("id", Integer.toString(ID));
                    estPosto.put("disponibilidade", String.valueOf(false));
                    estPosto.put("pacientes", Integer.toString((0)));
                    estPosto.put("enfermeiros", Integer.toString((result.getEnfermeiros())));
                    nodeSitPosto.dispPosto(retrofitInterface, estPosto, getApplicationContext(), false);
                    Toast.makeText(getApplicationContext(), "Posto Fechado", Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String, String> estPosto = new HashMap<>();
                    estPosto.put("id", Integer.toString(ID));
                    estPosto.put("disponibilidade", String.valueOf(true));
                    estPosto.put("pacientes", Integer.toString((0)));
                    estPosto.put("enfermeiros", Integer.toString((result.getEnfermeiros())));
                    nodeSitPosto.dispPosto(retrofitInterface, estPosto, getApplicationContext(), true);
                    Toast.makeText(getApplicationContext(), "Posto Aberto", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSair:
                Intent sair = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(sair);
                break;
        }
    }
}
