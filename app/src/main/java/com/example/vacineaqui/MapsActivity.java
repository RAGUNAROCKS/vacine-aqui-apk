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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.vacineaqui.databaseLocal.Create;
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
import java.util.Objects;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    public static RetrofitInterface retrofitInterface;
    private final String BASE_URL = "https://vacineaqui.herokuapp.com/";

    private static final String TAG = "test";
    SearchView pesquisaPosto;
    TextView textHints;
    FloatingActionMenu fabOpcoes;
    FloatingActionButton fabGeraRota, fabSituacao, fabUsuario;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).
                build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        criarBaselocal();
        initFabMenu();
    }

    private void initFabMenu(){
        fabOpcoes = findViewById(R.id.fabOpcoes);
        fabGeraRota = findViewById(R.id.fabGeraRota);
        fabSituacao = findViewById(R.id.fabSituacao);
        fabUsuario = findViewById(R.id.fabUsuario);
        pesquisaPosto = findViewById(R.id.pesquisaPosto);
        textHints = findViewById(R.id.textOptions);

        fabOpcoes.setOnMenuToggleListener(opened -> {
            Log.i(TAG, "on click: Opcoes do Aplicativo");
            if(!opened){
                mMap.clear();
                cameraLocal();
                textHints.setText("Clique no botão +");
            }else{
                 fabGeraRota.setLabelVisibility(View.VISIBLE);
                 fabSituacao.setLabelVisibility(View.VISIBLE);
                 fabUsuario.setLabelVisibility(View.VISIBLE);
                 textHints.setText("Selecione as opções");
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
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
        Objects.requireNonNull(mMap.addMarker(voce)).showInfoWindow();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            }
        }
    }

    private int postoIdeal(List<PostoDeVacina> pLista, LatLng voce){
        int size = pLista.size();
        double tam = -1;
        int np = 0;
        int fila = 0;
        for(int i=0; i < size; i++) {
            double aux = distance(voce, pLista.get(i).getPosicao());
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
        return np;
    }

    private void geraRota() {
        removeLabel();
        if(pesquisaPosto.getTranslationY() == 0f) pesquisaPosto.animate().translationY(-100).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        Update u = new Update(getApplicationContext());
        u.UpdateNode(retrofitInterface, getApplicationContext());
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        LatLng you = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.clear();
        MarkerOptions voce = new MarkerOptions().position(you).
                title("Você está aqui!").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        int np = postoIdeal(pLista, you);
        double tam = distance(voce.getPosition(), pLista.get(np).getPosicao());
        MarkerOptions posto = new MarkerOptions().position(pLista.get(np).getPosicao()).
                title(pLista.get(np).getNome()).snippet(customSnippet(pLista.get(np)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(posto.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posto.getPosition(),16));
        mMap.addMarker(voce);
        Objects.requireNonNull(mMap.addMarker(posto)).showInfoWindow();
        Toast.makeText(getApplicationContext(), (int) tam +"m de distancia",Toast.LENGTH_SHORT).show();
        textHints.setText("O Posto Ideal é...");
    }

    private void geraSituacao() {
        removeLabel();
        Update u = new Update(getApplicationContext());
        u.UpdateNode(retrofitInterface, getApplicationContext());
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
        textHints.setText("Postos Disponíveis");
    }

    private class searchFiltro implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            mMap.clear();
            Update u = new Update(getApplicationContext());
            u.UpdateNode(retrofitInterface, getApplicationContext());
            Read r = new Read(getApplicationContext());
            List<PostoDeVacina> PostoSm = r.buscarSemelhante(query);
            int size = PostoSm.size();
            if(size == 0){
                Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
            }else{
                LatLng you = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                int np = postoIdeal(PostoSm, you);
                MarkerOptions posto = new MarkerOptions().position(PostoSm.get(np).getPosicao()).
                        title(PostoSm.get(np).getNome()).snippet(customSnippet(PostoSm.get(np))).
                        icon(BitmapDescriptorFactory.defaultMarker(corMarcador(PostoSm.get(np).getDisponibilidade())));
                Objects.requireNonNull(mMap.addMarker(posto)).showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(PostoSm.get(np).getLatitude(), PostoSm.get(np).getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(PostoSm.get(np).getLatitude(), PostoSm.get(np).getLongitude()), 16));
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
        textHints.setText("Acesso Restrito");
        View v = getLayoutInflater().inflate(R.layout.login_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v).show();

        Button entrarBtn = v.findViewById(R.id.btnEntrar);
        EditText loginEdit = v.findViewById(R.id.lblLogin);
        EditText senhaEdit = v.findViewById(R.id.lblSenha);

        entrarBtn.setOnClickListener(v1 -> {
            HashMap<String, String> map = new HashMap<>();

            map.put("id", loginEdit.getText().toString());
            map.put("senha", senhaEdit.getText().toString());

            NodeConnection nodeConnection = new NodeConnection();
            nodeConnection.login(retrofitInterface, map, getApplicationContext());
            try { Thread.sleep (1000); finish();} catch (InterruptedException ex) {Log.e(String.valueOf(map.get("id")), "onClick: ", ex);}
        });
    }

    public void criarBaselocal(){
        Create c = new Create(getApplicationContext());
        c.createTable();
        Update u = new Update(getApplicationContext());
        List<PostoDeVacina> plista = new Read(getApplicationContext()).buscarTodos();
        if(plista.size() == 0)
            u.InserirNode(retrofitInterface, getApplicationContext());
        else
            u.UpdateNode(retrofitInterface, getApplicationContext());
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
                    TextView info = v.findViewById(R.id.infoTest);
                    TextView snippet = v.findViewById(R.id.SnippetTest);

                    info.setText(marker.getTitle());
                    snippet.setText(marker.getSnippet());
                    return v;
                }else{
                    return null;
                }
            }
        });
    }

    public String customSnippet(PostoDeVacina p){
        if(p.getDisponibilidade()) {
            return p.getInfo() + " \nNº de Pacientes: " + p.getPacientes() + " \nNº de Enfermeiros: " + p.getEnfermeiros();
        }else{
            return "VACINAÇÃO INDISPONIVEL";
        }
    }
    public float corMarcador(boolean d){if(d) return BitmapDescriptorFactory.HUE_RED; else return BitmapDescriptorFactory.HUE_YELLOW;}

    public static double distance(LatLng StartP, LatLng EndP) {
        float[] results = new float[1];
        Location.distanceBetween(StartP.latitude, StartP.longitude,
                EndP.latitude, EndP.longitude,
                results);
        return results[0];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabGeraRota:
                Log.i(TAG, "on click: Exibir o melhor posto");
                geraRota();
                break;
            case R.id.fabSituacao:
                Log.i(TAG, "on click: Exibir todos os postos");
                geraSituacao();
                break;
            case R.id.fabUsuario:
                Log.i(TAG, "on click: Exibir login");
                mostrarLogin();
                break;
        }
    }
}