package com.example.vacineaqui;

import com.google.android.gms.maps.model.LatLng;

public class PostoDeVacina {
    private int id;
    private String nome;
    private double latitude;
    private double longitude;
    private boolean disponibilidade;

    public int getId(){ return id; }
    public String getNome(){ return nome; }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }
    public boolean getDisponibilidade(){ return disponibilidade; }
    public LatLng getPosicao(){ return new LatLng(latitude,longitude);}

    public void setId(int id){ this.id = id; }
    public void setNome(String nome){ this.nome = nome; }
    public void setLatitude(double latitude){ this.latitude = latitude; }
    public void setLongitude(double longitude){ this.longitude = longitude; }
    public void setDisponibilidade(boolean disponibilidade){ this.disponibilidade = disponibilidade; }
    public void setPostoDeVacina(int id, String nome, double latitude, double longitude, boolean disponibilidade){
        this.id = id; this.nome = nome; this.latitude = latitude; this.longitude = longitude; this.disponibilidade = disponibilidade;
    }

}
