package com.example.vacineaqui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacineaqui.database.Read;
import com.example.vacineaqui.database.Update;

import java.util.List;

public class Filometro extends Activity implements View.OnClickListener {
    Button btnAddPacientes, btnSubPacientes, btnAddEnfermeiros, btnSubEnfermeiros, btnSalvar, btnFecharPosto, btnSair;
    TextView lblQtdPacientes, lblQtdEnfermeiros, postoText, PacientesText, EnfermeirosText, disponibilidadeText;
    private int QTDPACIENTES = 0, QTDENFERMEIROS = 0, ID, BACKUP;
    List<PostoDeVacina> pLista;

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

            Read r = new Read(getApplicationContext());
            pLista = r.buscarTodos();

            postoText = findViewById(R.id.postoText);
            PacientesText = findViewById(R.id.pacientesText);
            EnfermeirosText = findViewById(R.id.enfermeirosText);
            disponibilidadeText = findViewById(R.id.disponibilidadeText);

            postoText.setText(pLista.get(ID).getNome());
            PacientesText.setText(Integer.toString(pLista.get(ID).getPacientes()));
            EnfermeirosText.setText(Integer.toString(pLista.get(ID).getEnfermeiros()));
            disponibilidadeText.setText(vacinação(pLista.get(ID).getDisponibilidade()));

        }
    }

    public String vacinação(boolean d){ if(d) return "Sim"; else return "Não";}

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
                if(pLista.get(ID).getDisponibilidade()) QTDPACIENTES++;
                lblQtdPacientes.setText(Integer.toString(QTDPACIENTES));
                break;
            case R.id.btnSubPacientes:
                if(pLista.get(ID).getDisponibilidade()) QTDPACIENTES--;
                lblQtdPacientes.setText(Integer.toString(QTDPACIENTES));
                break;
            case R.id.btnAddEnfermeiros:
                if(pLista.get(ID).getDisponibilidade()) QTDENFERMEIROS++;
                lblQtdEnfermeiros.setText(Integer.toString(QTDENFERMEIROS));
                break;
            case R.id.btnSubEnfermeiros:
                if(pLista.get(ID).getDisponibilidade()) QTDENFERMEIROS--;
                lblQtdEnfermeiros.setText(Integer.toString(QTDENFERMEIROS));
                break;
            case R.id.btnSalvar:
                if(pLista.get(ID).getPacientes() + QTDPACIENTES >= 0 && pLista.get(ID).getEnfermeiros() + QTDENFERMEIROS > 0){
                    u.updateFila(pLista.get(ID),QTDPACIENTES,QTDENFERMEIROS);
                    Intent salvar = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(salvar);
                }else{
                    Toast.makeText(getApplicationContext(), "Redução ultrapassa a quantidade de pessoas", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnFecharPosto:
                if(pLista.get(ID).getDisponibilidade()){
                    pLista.get(ID).setPacientes(0);
                    pLista.get(ID).setDisponibilidade(false);
                    u.update(pLista.get(ID));
                    Toast.makeText(getApplicationContext(), "Posto Fechado", Toast.LENGTH_SHORT).show();
                }else{
                    pLista.get(ID).setDisponibilidade(true);
                    u.update(pLista.get(ID));
                    Toast.makeText(getApplicationContext(), "Posto Aberto", Toast.LENGTH_SHORT).show();
                }
                Intent fecharPosto = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(fecharPosto);
                break;
            case R.id.btnSair:
                Intent sair = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(sair);
                break;
        }
    }
}
