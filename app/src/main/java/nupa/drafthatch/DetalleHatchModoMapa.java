package nupa.drafthatch;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;

public class DetalleHatchModoMapa extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public ArrayList<Hatch> Hatchs = new ArrayList<Hatch>();
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private String username;
    private int idhatch;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    Location locacionUser=new Location("PuntoA");
    private MapView mMapView;
    private GoogleMap googleMap;
    HashMap<String, Integer> extraMarkerInfo = new HashMap<String, Integer>();
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static String name;
    static String email;
    static Uri photoUrl;
    static String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_hatch_modo_mapa);

        username= getIntent().getExtras().getString("username");
        userDistanceSetting=getIntent().getExtras().getString("distanceSetting");
        userCategoriesSetting = getIntent().getExtras().getString("categories");
        idhatch = getIntent().getIntExtra("Id_Hatch", 0);


        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.onResume();

// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // ...
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(getApplicationContext(),loginMenu.class);
                    // startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // ...

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){

            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl= user.getPhotoUrl();
            uid = user.getUid();
        }
    }

    public void listHatches() {

        DatabaseReference getHatches = FirebaseDatabase.getInstance().getReference().child("hatchs").child(String.valueOf(idhatch));
         getHatches.addListenerForSingleValueEvent(new ValueEventListener() {
            // final ArrayList<Hatch>output = new ArrayList<Hatch>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String hatchTitulo = (String) dataSnapshot.child("Titulo").getValue();
                final  String hatchBody = (String) dataSnapshot.child("Cuerpo").getValue();
                final  String hatchLat = (String) dataSnapshot.child("Latitud").getValue();
                final  String hatchLng = (String) dataSnapshot.child("Longitud").getValue();
                final  String hCategoria = (String) dataSnapshot.child("Categoria").getValue();

                LatLng LocacionHatch = new LatLng(Double.parseDouble(hatchLat), Double.parseDouble(hatchLng));

                Marker MarcadorHatch = googleMap.addMarker(new MarkerOptions()
                        .position(LocacionHatch)
                        .title(hatchTitulo)
                        .snippet(hatchBody)
                        .icon(BitmapDescriptorFactory.fromResource(setIcon(hCategoria))));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocacionHatch,18));




            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("The read failed: " + error.getMessage());
            }


        });
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap=Map;
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
        listHatches();
    }



    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public int setIcon(String categorias) {

        int iconMap = 0;

        switch (categorias) {
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

        return iconMap;
    }

}
