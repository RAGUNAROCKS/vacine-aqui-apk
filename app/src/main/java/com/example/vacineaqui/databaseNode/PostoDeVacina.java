package com.example.vacineaqui.databaseNode;

import com.google.android.gms.maps.model.LatLng;

public class PostoDeVacina {
    private int id;
    private String senha;
    private String nome;
    private double latitude;
    private double longitude;
    private boolean disponibilidade;
    private int pacientes;
    private int enfermeiros;

    public int getId(){ return id; }
    public String getSenha() { return senha; }
    public String getNome(){ return nome; }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }
    public boolean getDisponibilidade(){ return disponibilidade; }
    public int getPacientes(){ return pacientes; }
    public int getEnfermeiros(){ return enfermeiros; }
    public LatLng getPosicao(){ return new LatLng(latitude,longitude);}

    public void setId(int id){ this.id = id; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setNome(String nome){ this.nome = nome; }
    public void setLatitude(double latitude){ this.latitude = latitude; }
    public void setLongitude(double longitude){ this.longitude = longitude; }
    public void setDisponibilidade(boolean disponibilidade){ this.disponibilidade = disponibilidade; }
    public void setPacientes(int pacientes){ this.pacientes = pacientes; }
    public void setEnfermeiros(int enfermeiros){ this.enfermeiros = enfermeiros; }
    public void setPostoDeVacina(int id, String senha, String nome, double latitude, double longitude, boolean disponibilidade, int pacientes, int enfermeiros){
        this.id = id; this.senha = senha; this.nome = nome; this.latitude = latitude; this.longitude = longitude;
        this.disponibilidade = disponibilidade; this.pacientes = pacientes; this.enfermeiros = enfermeiros;
    }

}
