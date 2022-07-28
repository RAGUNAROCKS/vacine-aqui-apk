package com.example.vacineaqui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacineaqui.databaseNode.NodeConnection;
import com.example.vacineaqui.databaseNode.PostoDeVacina;

import java.util.HashMap;

public class Filometro extends Activity implements View.OnClickListener {
    Button btnAddPacientes, btnSubPacientes, btnAddEnfermeiros, btnSubEnfermeiros, btnSalvar, btnFecharPosto, btnSair;
    TextView lblQtdPacientes, lblQtdEnfermeiros, postoText, pacientesText, enfermeirosText, disponibilidadeText;
    private int QTDPACIENTES = 0, QTDENFERMEIROS = 0, ID; PostoDeVacina result;
    public static boolean connected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filometro_dialog);

        modPacientes();
        modEnfermeiros();
        modAcoes();

        Intent filometro = getIntent();
        Bundle parametros = filometro.getExtras();

        if(parametros != null) {
            ID = parametros.getInt("ID");
            result = new PostoDeVacina();
            result.setPostoDeVacina(ID,
                    "******",
                    parametros.getString("NOME"),
                    0,
                    0,
                    parametros.getBoolean("DISPONIBILIDADE"),
                    parametros.getInt("PACIENTES"),
                    parametros.getInt("ENFERMEIROS"),
                    parametros.getString("INFO"));
            mostrarDados();
        }
    }

    private String vacinacao(boolean d){ if(d) return "Aberto"; else return "Fechado";}

    private void mostrarDados(){
        postoText = findViewById(R.id.postoText);
        pacientesText = findViewById(R.id.pacientesText);
        enfermeirosText = findViewById(R.id.enfermeirosText);
        disponibilidadeText = findViewById(R.id.disponibilidadeText);

        postoText.setText(result.getNome());
        pacientesText.setText(Integer.toString(result.getPacientes()));
        enfermeirosText.setText(Integer.toString(result.getEnfermeiros()));
        disponibilidadeText.setText(vacinacao(result.getDisponibilidade()));
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
                    nodeSalvar.salvarFilometro(MapsActivity.retrofitInterface, salvar, getApplicationContext());
                }else{
                    Toast.makeText(getApplicationContext(), "Redução ultrapassa a quantidade de pessoas", Toast.LENGTH_SHORT).show();
                }
                if (connected) {
                    finish();
                    connected = false;
                }
                break;
            case R.id.btnFecharPosto:
                NodeConnection nodeSitPosto = new NodeConnection();
                if(result.getDisponibilidade()){
                    HashMap<String, String> estPosto = new HashMap<>();
                    estPosto.put("id", Integer.toString(ID));
                    estPosto.put("disponibilidade", String.valueOf(false));
                    estPosto.put("pacientes", Integer.toString((0)));
                    estPosto.put("enfermeiros", Integer.toString((result.getEnfermeiros())));
                    nodeSitPosto.dispPosto(MapsActivity.retrofitInterface, estPosto, getApplicationContext(), false);
                    Toast.makeText(getApplicationContext(), "Posto Fechado", Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String, String> estPosto = new HashMap<>();
                    estPosto.put("id", Integer.toString(ID));
                    estPosto.put("disponibilidade", String.valueOf(true));
                    estPosto.put("pacientes", Integer.toString((0)));
                    estPosto.put("enfermeiros", Integer.toString((result.getEnfermeiros())));
                    nodeSitPosto.dispPosto(MapsActivity.retrofitInterface, estPosto, getApplicationContext(), true);
                    Toast.makeText(getApplicationContext(), "Posto Aberto", Toast.LENGTH_SHORT).show();
                }
                if (connected) {
                    finish();
                    connected = false;
                }
                break;
            case R.id.btnSair:
                Intent sair = new Intent(getApplicationContext(), MapsActivity.class);
                sair.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sair);
                finish();
                break;
        }
    }
}