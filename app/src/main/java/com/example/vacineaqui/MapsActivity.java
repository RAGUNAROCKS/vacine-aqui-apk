package com.example.vacineaqui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.example.vacineaqui.database.Create;
import com.example.vacineaqui.database.Delete;
import com.example.vacineaqui.database.Read;
import com.example.vacineaqui.database.Update;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{
    private static final String TAG = "test" ;
    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static final int TAM = 29;
    boolean isMenuOpen = false;
    FloatingActionButton fabOpcoes, fabGeraRota, fabSituacao;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initFabMenu();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        Create c = new Create(getApplicationContext());
        c.createTable();
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        if(pLista.size() == 0){
            inserirPosto();
        }
    }

    private void initFabMenu(){
        fabOpcoes = findViewById(R.id.fabOpcoes);
        fabGeraRota = findViewById(R.id.fabGeraRota);
        fabSituacao = findViewById(R.id.fabSituacao);

        fabGeraRota.setAlpha(0f);
        fabSituacao.setAlpha(0f);

        fabGeraRota.setTranslationY(100f);
        fabSituacao.setTranslationY(100f);

        fabOpcoes.setOnClickListener(this);
        fabGeraRota.setOnClickListener(this);
        fabSituacao.setOnClickListener(this);
    }

    private void openMenu(){
        isMenuOpen = !isMenuOpen;

        fabOpcoes.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
        fabGeraRota.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabSituacao.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu(){
        isMenuOpen = !isMenuOpen;

        fabOpcoes.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        fabGeraRota.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabSituacao.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

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
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()+""+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);
                }
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
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    public void geraRota(View view) {
        Read r = new Read(getApplicationContext());
        List<PostoDeVacina> pLista = r.buscarTodos();
        int size = pLista.size();
        LatLng you = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions voce = new MarkerOptions().position(you).
                title("Você está aqui!").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        double tam = 0;
        int np = 0;
        for(int i=0; i < size; i++) {
            double aux = distance(voce.getPosition(), pLista.get(i).getPosicao());
            if (tam == 0 && pLista.get(i).getDisponibilidade()) {
                tam = aux;
                np = i;
            } else if (tam > aux && pLista.get(i).getDisponibilidade()) {
                tam = aux;
                np = i;
            }
            }
            MarkerOptions posto = new MarkerOptions().position(pLista.get(np).getPosicao()).
                    title(pLista.get(np).getNome()).snippet(vacinação(pLista.get(np).getDisponibilidade()));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(posto.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posto.getPosition(),16));
            mMap.addMarker(posto).showInfoWindow();
    }

    public void inserirPosto(){
        PostoDeVacina[] PDV = new PostoDeVacina[TAM];
        for(int i = 0; i < PDV.length; i++) PDV[i] = new PostoDeVacina();
        PDV[0].setPostoDeVacina(0, "PAF Ondina", -13.00030008513721, -38.507768947459226, true);
        PDV[1].setPostoDeVacina(1, "Parque de Exposições", -12.92364654244234, -38.36048968608761, true);
        PDV[2].setPostoDeVacina(2, "Fundação Bahiana para Desenvolvimento das Ciências - Brotas (Drive-Thru)",-12.990448551500851, -38.48803517429645, true);
        PDV[3].setPostoDeVacina(3, "Vila Militar (Dendezeiros)", -12.9291824418218, -38.510577087384775, true);
        PDV[4].setPostoDeVacina(4, "Fundação Bahiana para Desenvolvimento das Ciências - Cabula", -12.9291824418218, -38.510577087384775, true);
        PDV[5].setPostoDeVacina(5, "Universidade Católica do Salvador- Pituaçu (Drive-Thru)", -12.948983057195388, -38.41334810285682, true);
        PDV[6].setPostoDeVacina(6, "Unijorge Paralela (Drive-Thru)", -12.937447748303939, -38.4100197742972, true);
        PDV[7].setPostoDeVacina(7, "5º Centro de Saúde Clementino Fraga", -12.99090534344484, -38.51115501662535, true);
        PDV[8].setPostoDeVacina(8, "Arena Fonte Nova", -12.976979075163092, -38.50396001847425, true);
        PDV[9].setPostoDeVacina(9, "Centro de Convenções", -12.983494899902636, -38.432440772447784, true);
        PDV[10].setPostoDeVacina(10, "Barradão - Esporte Clube Vitória (Drive-Thru)", -12.918144323422489, -38.42646155923109, true);
        PDV[11].setPostoDeVacina(11, "UNIFTC - Paralela  (Drive Thru)", -12.934506034655314, -38.392616677443364, true);
        PDV[12].setPostoDeVacina(12, "Atacadão Atakarejo", -12.846296855504033, -38.45553536131372, true);
        PDV[13].setPostoDeVacina(13, "USF San Martim III", -12.94951576651817, -38.47883339888328, true);
        PDV[14].setPostoDeVacina(14, "USF da Federação", -12.995116963731828, -38.5021751761453, true);
        PDV[15].setPostoDeVacina(15, "Colégio da Polícia Militar - CPM (Dendezeiros)", -12.929522288607963, -38.50920379684093,true);
        PDV[16].setPostoDeVacina(16, "Universidade Católica do Salvador- Pituaçu (Posto Fixo)", -12.948273602264669, -38.4133785031326, true);
        PDV[17].setPostoDeVacina(17, "Fundação Bahiana para Desenvolvimento das Ciências - Brotas (Posto Fixo)",-12.990310556734403, -38.487936358954265, true);
        PDV[18].setPostoDeVacina(18, "5º Centro de Saúde Clementino Fraga", -12.990915797684053, -38.511187203132, true);
        PDV[19].setPostoDeVacina(19, "Unijorge Paralela (Posto Fixo)", -12.937447748303939, -38.4100197742972, true);
        PDV[20].setPostoDeVacina(20, "Barradão - Esporte Clube Vitória (Posto Fixo)", -12.918052856934004, -38.426513555526945, true);
        PDV[21].setPostoDeVacina(21, "USF Claudelino Miranda Resgate", -12.96077987442304, -38.461497504317364, true);
        PDV[22].setPostoDeVacina(22, "USF Vista Alegre", -12.851485749199778, -38.46184067429859, true);
        PDV[23].setPostoDeVacina(23, "USF Plataforma", -12.900447498201588, -38.48673617429789, true);
        PDV[24].setPostoDeVacina(24, "USF Colinas De Periperi", -12.900447498201588, -38.48673617429789, true);
        PDV[25].setPostoDeVacina(25, "USF Santa Luzia", -12.988242586527642, -38.50128516080319, true);
        PDV[26].setPostoDeVacina(26, "USF Cajazeiras X", -12.892302781863219, -38.41073004731126,true);
        PDV[27].setPostoDeVacina(27, "UBS Nelson Piauhy Dourado", -12.886003145916707, -38.43123929943591, true);
        PDV[28].setPostoDeVacina(28, "UNIFTC - Paralela (Posto Fixo)", -12.934806319528237, -38.392217674297186, true);
        for(int j = 0; j < PDV.length; j++){
            Update u = new Update(getApplicationContext());
            u.inserir(PDV[j]);
        }
    }

    public void deletarBase(){
        Delete d = new Delete(getApplicationContext());
        d.deleteTable();
    }

    public String vacinação(boolean d){ if(d) return "Vacinação Disponivel"; else return "Vacinação Indisponivel";}

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
                Log.i(TAG, "on click: fabopcoes");
                if(isMenuOpen){
                    closeMenu();
                }else{
                    openMenu();
                }
                break;
            case R.id.fabGeraRota:
                Log.i(TAG, "on click: gerar rota");
                geraRota(v);
                break;
            case R.id.fabSituacao:
                Log.i(TAG, "on click: Inserir localização");
                deletarBase();
                break;
        }
    }
}