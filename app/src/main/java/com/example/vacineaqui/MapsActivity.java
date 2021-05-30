package com.example.vacineaqui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private Retrofit retrofit;
    public static RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://vacineaqui.herokuapp.com/";


    private static final String TAG = "test";
    SearchView pesquisaPosto;
    FloatingActionMenu fabOpcoes;
    FloatingActionButton fabGeraRota, fabSituacao, fabUsuario;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).
                build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        deletarBase();
        initFabMenu();
    }

    private void initFabMenu(){
        fabOpcoes = findViewById(R.id.fabOpcoes);
        fabGeraRota = findViewById(R.id.fabGeraRota);
        fabSituacao = findViewById(R.id.fabSituacao);
        fabUsuario = findViewById(R.id.fabUsuario);
        pesquisaPosto = findViewById(R.id.pesquisaPosto);

        fabOpcoes.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                Log.i(TAG, "on click: Opcoes do Aplicativo");
                if(!opened){
                    mMap.clear();
                    cameraLocal();
                }else{
                     fabGeraRota.setLabelVisibility(View.VISIBLE);
                     fabSituacao.setLabelVisibility(View.VISIBLE);
                     fabUsuario.setLabelVisibility(View.VISIBLE);
                }
            }
        });
        pesquisaPosto.setAlpha(0f);
        pesquisaPosto.setTranslationY(-100f);

        fabOpcoes.setOnClickListener(this);
        fabGeraRota.setOnClickListener(this);
        fabSituacao.setOnClickListener(this);
        fabUsuario.setOnClickListener(this);
    }

    private void removeLabel(){
        fabGeraRota.setLabelVisibility(View.GONE);
        fabSituacao.setLabelVisibility(View.GONE);
        fabUsuario.setLabelVisibility(View.GONE);
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
        customInfoWindow();
    }

    private void cameraLocal(){
        if(pesquisaPosto.getTranslationY() == 0f) pesquisaPosto.animate().translationY(-100).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
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
        }
    }

    private void geraRota(View view) {
        removeLabel();
        if(pesquisaPosto.getTranslationY() == 0f) pesquisaPosto.animate().translationY(-100).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        int size = pLista.size();
        LatLng you = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.clear();
        MarkerOptions voce = new MarkerOptions().position(you).
                title("Você está aqui!").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        double tam = -1;
        int np = 0;
        int fila = 0;
        for(int i=0; i < size; i++) {
            double aux = distance(voce.getPosition(), pLista.get(i).getPosicao());
            int rel = pLista.get(i).getPacientes() / pLista.get(i).getEnfermeiros();
            if (tam == -1 && pLista.get(i).getDisponibilidade()) {
                tam = aux;
                np = i;
                fila = rel;
            } else if (tam + fila * tam >= aux + rel * aux && pLista.get(i).getDisponibilidade()) {
                    tam = aux;
                    np = i;
                    fila = rel;
            }
        }
        MarkerOptions posto = new MarkerOptions().position(pLista.get(np).getPosicao()).
                title(pLista.get(np).getNome()).snippet(customSnippet(pLista.get(np)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(posto.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posto.getPosition(),16));
        mMap.addMarker(voce);
        mMap.addMarker(posto).showInfoWindow();
        Toast.makeText(getApplicationContext(), (int) tam +"m de distancia",Toast.LENGTH_SHORT).show();
    }

    private void geraSituacao(View view) {
        removeLabel();
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        int size = pLista.size();
        mMap.clear();
        for(int i=0; i < size; i++) {
            MarkerOptions posto = new MarkerOptions().position(pLista.get(i).getPosicao()).
                    title(pLista.get(i).getNome()).snippet(customSnippet(pLista.get(i))).
                    icon(BitmapDescriptorFactory.defaultMarker(corMarcador(pLista.get(i).getDisponibilidade())));
            mMap.addMarker(posto);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(-12.92, -38.44)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.92, -38.44), (float) 11.3));
        pesquisaPosto.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        pesquisaPosto.setOnQueryTextListener(new searchFiltro());
    }

    private class searchFiltro implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            mMap.clear();
            Read r = new Read(getApplicationContext());
            List<PostoDeVacina> PostoSm = r.buscarSemelhante(query);
            if(PostoSm.size() == 0){
                Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
            }else{
                MarkerOptions posto = new MarkerOptions().position(PostoSm.get(0).getPosicao()).
                        title(PostoSm.get(0).getNome()).snippet(customSnippet(PostoSm.get(0))).
                        icon(BitmapDescriptorFactory.defaultMarker(corMarcador(PostoSm.get(0).getDisponibilidade())));
                mMap.addMarker(posto).showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(PostoSm.get(0).getLatitude(), PostoSm.get(0).getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(PostoSm.get(0).getLatitude(), PostoSm.get(0).getLongitude()), 16));
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mMap.clear();
            Read r = new Read(getApplicationContext());
            List<PostoDeVacina> PostoSm = r.buscarSemelhante(newText);
            if(PostoSm.size() == 0){
                Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
            }else {
                for (int i = 0; i < PostoSm.size(); i++) {
                    MarkerOptions posto = new MarkerOptions().position(PostoSm.get(i).getPosicao()).
                            title(PostoSm.get(i).getNome()).snippet(customSnippet(PostoSm.get(i))).
                            icon(BitmapDescriptorFactory.defaultMarker(corMarcador(PostoSm.get(i).getDisponibilidade())));
                    mMap.addMarker(posto);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(-12.92, -38.44)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.92, -38.44), (float) 11.3));
                }
            }
            return false;
        }
    }

    private void mostrarLogin(){
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
        Create c = new Create(getApplicationContext());
        c.createTable();
        Update u = new Update(getApplicationContext());
        u.InserirNode(retrofitInterface, getApplicationContext());
    }

    public void customInfoWindow(){
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if(marker.getSnippet() != null){
                    View v = getLayoutInflater().inflate(R.layout.janela_info_dialog,null);
                    TextView info=(TextView) v.findViewById(R.id.infoTest);
                    TextView snippet=(TextView) v.findViewById(R.id.SnippetTest);

                    info.setText(marker.getTitle());
                    snippet.setText(marker.getSnippet());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vacinahoramarcada.saude.salvador.ba.gov.br/")));
                        }
                    });
                    return v;
                }else{
                    return null;
                }
            }
        });
    }

    public String customSnippet(PostoDeVacina p){
        if(p.getDisponibilidade())
            return p.getInfo() + " \nNº de Pacientes: " + p.getPacientes() + " \nNº de Enfermeiros: " + p.getEnfermeiros();
        else
            return "Indisponivel";
    }
    public float corMarcador(boolean d){if(d) return BitmapDescriptorFactory.HUE_RED; else return BitmapDescriptorFactory.HUE_YELLOW;}

    public static double distance(LatLng StartP, LatLng EndP) {
        float[] results = new float[1];
        Location.distanceBetween(StartP.latitude, StartP.longitude,
                EndP.latitude, EndP.longitude,
                results);
        double resultado = Double.valueOf(results[0]);
        return resultado;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabGeraRota:
                Log.i(TAG, "on click: Exibir o melhor posto");
                geraRota(v);
                break;
            case R.id.fabSituacao:
                Log.i(TAG, "on click: Exibir todos os postos");
                geraSituacao(v);
                break;
            case R.id.fabUsuario:
                Log.i(TAG, "on click: Exibir login");
                mostrarLogin();
                break;
        }
    }
}