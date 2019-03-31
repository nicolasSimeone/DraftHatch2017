package nupa.drafthatch;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NuevoHatchStep2 extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;
    private Geocoder geocoder;
    private List<Address> addresses=null;
    private AutoCompleteTextView autoCompleteUbicacion;
    private String direccion, direccionFinal;

    private int User_Id;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    static String username;
    private String TitleField, DetailsField;
    private String categorias;
    private String participantes;
    private String Tipo;
    private int iconMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hatch_step2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNuevoHatchMap);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Elige la ubicación!");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final Intent intent = getIntent();

        username = intent.getStringExtra("username");
        userDistanceSetting = intent.getStringExtra("distanceSetting");
        userCategoriesSetting = intent.getStringExtra("categories");
        User_Id = intent.getIntExtra("User_Id", 0);
        participantes = intent.getStringExtra("Participantes");
        categorias = intent.getStringExtra("Categoria");
        TitleField = intent.getStringExtra("Titulo");
        DetailsField = intent.getStringExtra("Detalle");
        Tipo = intent.getStringExtra("Tipo");

        int id = getResources().getIdentifier("nupa.drafthatch:drawable/step2", null, null);
        setIcon();
        geocoder = new Geocoder(this, Locale.getDefault());

        autoCompleteUbicacion = (AutoCompleteTextView) findViewById(R.id.autoCompleteUbicacion);
        GooglePlacesAutocompleteAdapter places = new GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_list_item_1);
        autoCompleteUbicacion.setAdapter(places);

        autoCompleteUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteUbicacion.setText("");
            }
        });

        autoCompleteUbicacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                map.clear();
                direccion = autoCompleteUbicacion.getText().toString();
                try {
                    addresses = geocoder.getFromLocationName(direccion, 5);
                    if (addresses.size() > 0) {
                        Double lat = (double) (addresses.get(0).getLatitude());
                        Double lon = (double) (addresses.get(0).getLongitude());
                        latLng = new LatLng(lat, lon);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                direccionFinal = direccion.substring(0, direccion.indexOf(','));

                autoCompleteUbicacion.setText(direccionFinal);

                Marker direccionUsuario = map.addMarker(new MarkerOptions()
                        .position(latLng).icon(BitmapDescriptorFactory.fromResource(iconMap)).title("Presione si su evento es aqui"));

                direccionUsuario.showInfoWindow();

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        //startActivity
                        nuevoHatchStep3();
                    }
                });
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mapFragment.getMapAsync(this);
        setUpMapIfNeeded();
        //setUpMap();


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            // map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync();
            // Check if we were successful in obtaining the map.

            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap()  {



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {final boolean flag = true;

        map = googleMap;
        map.setMyLocationEnabled(true);
        // Check if we were successful in obtaining the map.
        if (map != null) {
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                boolean flag = true;
                @Override
                public void onMyLocationChange(final Location arg0) {
                    // TODO Auto-generated method stub

                    while(flag) {
                        Marker direccionUsuario = map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("presione si su evento es aqui").icon(BitmapDescriptorFactory.fromResource(iconMap)));
                        direccionUsuario.showInfoWindow();
                        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                            public void onInfoWindowClick(Marker marker) {
                                    try {
                                        addresses = geocoder.getFromLocation(arg0.getLatitude(),arg0.getLongitude(),1);
                                        direccionFinal = addresses.get(0).getAddressLine(0);
                                        nuevoHatchStep3();
                                    }catch (IOException e){

                                    }
                                }
                            });

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        flag = false;
                    }
                }
            });


            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng point) {
                    map.clear();
                    latLng = point;
                    Marker direccionUsuario = map.addMarker(new MarkerOptions().position(point).title("Presione si su evento es aqui").icon(BitmapDescriptorFactory.fromResource(iconMap)));
                    direccionUsuario.showInfoWindow();
                    try {
                        addresses = geocoder.getFromLocation(point.latitude,point.longitude,1);
                        direccionFinal = addresses.get(0).getAddressLine(0);
                        autoCompleteUbicacion.setText(direccionFinal);

                    }catch (IOException e){

                    }
                    map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        public void onInfoWindowClick(Marker marker) {
                            nuevoHatchStep3();
                        }
                    });
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }
            });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if(mLastLocation!=null){
            latLng=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        }

        if(latLng !=null){
            // setUpMap();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void setIcon(){

        switch (categorias){
            case "Activismo":
                iconMap = R.drawable.activismo_icon_map;
                break;
            case "Flash":
                iconMap = R.drawable.flash_icon_map;
                break;
            case "Mascotas":
                iconMap = R.drawable.mascostas_icon_map;
                break;
            case "Deportes":
                iconMap = R.drawable.deportes_icon_map;
                break;
            case "Salidas":
                iconMap = R.drawable.salidas_icon_map;
                break;
            case "Musica":
                iconMap = R.drawable.musica_icon_map;
                break;
        }

    }

    public void nuevoHatchStep3(){

        Intent step3 = new Intent(NuevoHatchStep2.this, NuevoHatchStep3.class);

        step3.putExtra("Titulo", TitleField);
        step3.putExtra("Detalle", DetailsField);
        step3.putExtra("Categoria", categorias);
        step3.putExtra("username", username);
        step3.putExtra("User_Id", User_Id);
        step3.putExtra("categories", userCategoriesSetting);
        step3.putExtra("distanceSetting", userDistanceSetting);
        step3.putExtra("Participantes", participantes);
        step3.putExtra("Tipo", Tipo);

        step3.putExtra("Latitud", Double.toString(latLng.latitude));
        step3.putExtra("Longitud", Double.toString(latLng.longitude));
        step3.putExtra("Direccion", direccionFinal);
        startActivity(step3);

    }


}
