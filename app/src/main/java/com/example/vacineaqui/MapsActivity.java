package com.example.vacineaqui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.vacineaqui.databaseLocal.Create;
import com.example.vacineaqui.databaseLocal.Delete;
import com.example.vacineaqui.databaseLocal.Read;
import com.example.vacineaqui.databaseLocal.Update;
import com.example.vacineaqui.databaseNode.NodeConnection;
import com.example.vacineaqui.databaseNode.PostoDeVacina;
import com.example.vacineaqui.databaseNode.RetrofitInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private static final int REQUEST_CODE = 101;
    private static final int LOGIN_CODE = 100;

    private Retrofit retrofit;
    public static RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://vacineaqui.herokuapp.com/";

    boolean isMenuOpen = false;
    private static final String TAG = "test";
    FloatingActionButton fabOpcoes, fabGeraRota, fabSituacao, fabUsuario;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initFabMenu();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).
                build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Create c = new Create(getApplicationContext());
        c.createTable();
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        Update u = new Update(getApplicationContext());
        if(pLista.size() == 0){
            u.InserirNode(retrofitInterface, getApplicationContext());
        }else{
            u.UpdateNode(retrofitInterface, getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
    }

    private void initFabMenu(){
        fabOpcoes = findViewById(R.id.fabOpcoes);
        fabGeraRota = findViewById(R.id.fabGeraRota);
        fabSituacao = findViewById(R.id.fabSituacao);
        fabUsuario = findViewById(R.id.fabUsuario);

        fabGeraRota.setAlpha(0f);
        fabSituacao.setAlpha(0f);
        fabUsuario.setAlpha(0f);

        fabGeraRota.setTranslationY(100f);
        fabSituacao.setTranslationY(100f);
        fabUsuario.setTranslationY(100f);

        fabOpcoes.setOnClickListener(this);
        fabGeraRota.setOnClickListener(this);
        fabSituacao.setOnClickListener(this);
        fabUsuario.setOnClickListener(this);
    }

    private void openMenu(){
        isMenuOpen = !isMenuOpen;

        fabOpcoes.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
        fabGeraRota.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabSituacao.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabUsuario.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu(){
        isMenuOpen = !isMenuOpen;

        fabOpcoes.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        fabGeraRota.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabSituacao.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabUsuario.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

        mMap.clear();
        cameraLocal();
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null){
                currentLocation = location;
                Toast.makeText(getApplicationContext(), currentLocation.getLatitude()+","+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert mapFragment != null;
                mapFragment.getMapAsync(MapsActivity.this);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        cameraLocal();
    }

    private void cameraLocal(){
        LatLng you = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions voce = new MarkerOptions().position(you).
                title("Você está aqui!").icon(BitmapDescriptorFactory.
                defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(you));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(you,12));
        mMap.addMarker(voce).showInfoWindow();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            }
        }else if(requestCode == LOGIN_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrarLogin();
            }
        }
    }

    public void geraRota(View view) {
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        int size = pLista.size();
        LatLng you = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.clear();
        MarkerOptions voce = new MarkerOptions().position(you).
                title("Você está aqui!").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        double tam = 0;
        int np = 0;
        int fila = 0;
        for(int i=0; i < size; i++) {
            double aux = distance(voce.getPosition(), pLista.get(i).getPosicao());
            int rel = pLista.get(i).getPacientes() / pLista.get(i).getEnfermeiros();
            if (tam == 0 && pLista.get(i).getDisponibilidade()) {
                tam = aux;
                np = i;
                fila = rel;
            } else if (tam > aux && pLista.get(i).getDisponibilidade()) {
                if (fila >= rel){
                    tam = aux;
                    np = i;
                    fila = rel;
                }
            }
        }
        MarkerOptions posto = new MarkerOptions().position(pLista.get(np).getPosicao()).
                title(pLista.get(np).getNome()).snippet(vacinacao(pLista.get(np).getDisponibilidade()) + " " + pLista.get(np).getPacientes());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(posto.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posto.getPosition(),16));
        mMap.addMarker(voce);
        mMap.addMarker(posto).showInfoWindow();
        Toast.makeText(getApplicationContext(), (int) tam +"m de distancia",Toast.LENGTH_SHORT).show();
    }

    public void geraSituacao(View view) {
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        int size = pLista.size();
        mMap.clear();
        for(int i=0; i < size; i++) {
            MarkerOptions posto = new MarkerOptions().position(pLista.get(i).getPosicao()).
                    title(pLista.get(i).getNome()).snippet(vacinacao(pLista.get(i).getDisponibilidade()) + pLista.get(i).getPacientes()).
                    icon(BitmapDescriptorFactory.defaultMarker(corMarcador(pLista.get(i).getDisponibilidade())));
            mMap.addMarker(posto);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(-12.94, -38.44)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.94, -38.44), (float) 11.3));
    }

    private void mostrarLogin(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, LOGIN_CODE);
            return;
        }
        View v = getLayoutInflater().inflate(R.layout.login_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v).show();

        Button entrarBtn = v.findViewById(R.id.btnEntrar);
        EditText loginEdit = v.findViewById(R.id.lblLogin);
        EditText senhaEdit = v.findViewById(R.id.lblSenha);

        entrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();

                map.put("id", loginEdit.getText().toString());
                map.put("senha", senhaEdit.getText().toString());

                NodeConnection nodeConnection = new NodeConnection();
                nodeConnection.login(retrofitInterface, map, getApplicationContext());
            }
        });

    }

    public void deletarBase(){
        Delete d = new Delete(getApplicationContext());
        d.deleteTable();
    }

    public String vacinacao(boolean d){ if(d) return "Vacinação Disponivel / Numero de Pacientes:"; else return "Vacinação Indisponivel / Numero de Pacientes:";}
    public float corMarcador(boolean d){if(d) return BitmapDescriptorFactory.HUE_RED; else return BitmapDescriptorFactory.HUE_YELLOW;}

    public static double distance(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabOpcoes:
                Log.i(TAG, "on click: Opcoes do Aplicativo");
                if(isMenuOpen){
                    closeMenu();
                }else{
                    openMenu();
                }
                break;
            case R.id.fabGeraRota:
                Log.i(TAG, "on click: Exibir a menor rota");
                geraRota(v);
                break;
            case R.id.fabSituacao:
                Log.i(TAG, "on click: Exibir os postos");
                geraSituacao(v);
                break;
            case R.id.fabUsuario:
                Log.i(TAG, "on click: Exibir login");
                //deletarBase();
                mostrarLogin();
                break;
        }
    }
}