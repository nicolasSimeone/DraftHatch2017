package nupa.drafthatch;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    //Handle multiple markers on same location
    static HashMap<String, String> markerLocation;    // HashMap of marker identifier and its location as a string

    static final float COORDINATE_OFFSET = 0.00005f;
    //
    public ArrayList<Hatch> Hatchs = new ArrayList<Hatch>();
    private LatLng latLng;
    private String username;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    Location locacionUser=new Location("PuntoA");
    ClusterManager<Hatch> hatchClusterManager;

    HashMap<String, Integer> extraMarkerInfo = new HashMap<String, Integer>();

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static String name;
    static String email;
    static Uri photoUrl;
    static String uid;

    double distanciaM;

    private MapView mMapView;
    private GoogleMap googleMap;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        markerLocation=new HashMap<String, String>();

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapFragment);
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

        username= user.getUid();
       // googleMap = mMapView.getMap();
        locacionUser=((MainScreen)getActivity()).locacionUserPuntoA; //El manejo del GPS lo hago hago en la actividad que contiene los fragments
        latLng=new LatLng(locacionUser.getLatitude(),locacionUser.getLongitude());

        listHatches();
        return view;
    }

    public void listHatches() {

        DatabaseReference getHatches = FirebaseDatabase.getInstance().getReference().child("hatchs");
        final List<Hatch> output = new ArrayList<Hatch>();


        getHatches.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String hatchID = (String) messageSnapshot.child("HatchID").getValue();
                    String hatchTitulo = (String) messageSnapshot.child("Titulo").getValue();
                    String hatchEstado = (String) messageSnapshot.child("Status").getValue();
                    String hatchBody = (String) messageSnapshot.child("Cuerpo").getValue();
                    String hatchLat = (String) messageSnapshot.child("Latitud").getValue();
                    String hatchLng = (String) messageSnapshot.child("Longitud").getValue();
                    String hatchusername = (String) messageSnapshot.child("User_Name").getValue();
                    String hatchdireccion = (String) messageSnapshot.child("Direccion").getValue();
                    String hatchtipo = (String) messageSnapshot.child("Tipo").getValue();
                    String hatchparticipantes = (String) messageSnapshot.child("Participantes").getValue();
                    String hatchfechainicio = (String) messageSnapshot.child("FechaInicio").getValue();
                    String hatchfechafin = (String) messageSnapshot.child("FechaFin").getValue();
                    String hCategoria = (String) messageSnapshot.child("Categoria").getValue();
                    String hUserId = (String) messageSnapshot.child("User_Id").getValue();
                    String hUserEmail = (String)messageSnapshot.child("User_Email").getValue();
                    Long hEpochStart = (Long)messageSnapshot.child("EpochStart").getValue();
                    Long hEpochEnd = (Long)messageSnapshot.child("EpochFin").getValue();

                    Location locacionUserPuntoB = new Location("PuntoB");
                    locacionUserPuntoB.setLatitude(Double.parseDouble(hatchLat));
                    locacionUserPuntoB.setLongitude(Double.parseDouble(hatchLng));

                    double Distance = locacionUser.distanceTo(locacionUserPuntoB);
                    distanciaM = Distance;

                    if (hatchEstado.equals("Abierto")) {

                        Hatch h = new Hatch(hatchID, hatchTitulo, hatchBody, hatchLat, hatchLng, hatchusername, hatchdireccion, hatchtipo, Integer.parseInt(hatchID), Integer.parseInt(hatchparticipantes), hatchfechainicio, hatchfechafin, hatchEstado, /*Double.parseDouble(hDistancia)*/distanciaM, hCategoria, hUserId, hUserEmail, hEpochStart,hEpochEnd);
                        output.add(h);
                    }

                }

                for (int i = 0; i < output.size(); i++) {
                    Hatchs.add(output.get(i));
                }

                hatchClusterManager = new ClusterManager<Hatch>(getApplicationContext(),googleMap);

                for (int i = 0; i < Hatchs.size(); i++) {
                    Hatch hatch = new Hatch();
                    hatch = Hatchs.get(i);
                    markerLocation.put(String.valueOf(hatch.getUbicacion_lat()),String.valueOf(hatch.getUbicacion_lon()));
                    hatchClusterManager.addItem(hatch);

                }

                hatchClusterManager.cluster();


                googleMap.setOnCameraIdleListener(hatchClusterManager);
                hatchClusterManager.setRenderer(new renderIcon(getApplicationContext(), googleMap,
                        hatchClusterManager));

                googleMap.setOnInfoWindowClickListener(hatchClusterManager);
                googleMap.setOnMarkerClickListener(hatchClusterManager);

                //listener de cuando toco el cluster
                hatchClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Hatch>() {
                    @Override
                    public boolean onClusterClick(Cluster<Hatch> cluster) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                cluster.getPosition(), (float) Math.floor(googleMap
                                        .getCameraPosition().zoom + 3)), 300,
                                null);
                        return true;
                    }
                });

                //listener de cada item del cluster (hatch)
                hatchClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Hatch>() {
                    @Override
                    public void onClusterItemInfoWindowClick(Hatch hatch) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), ActividadDetalleHatch.class);

                        String TextTitulo = hatch.getTitle();
                        String TextBody = hatch.getBody();
                        Double Distancia = hatch.getDistancia();
                        String UserNameHatch = hatch.getUsername();
                        String Direccion = hatch.getDireccion();
                        String Tipo = hatch.getTipo();
                        int Id_Hatch = hatch.getId_Hatch();
                        String Fecha = hatch.getFecha();
                        String FechaInicio = hatch.getFechaInicio();
                        String FechaFin = hatch.getFechaFin();
                        int ParticipantesRequeridos = hatch.getParticipantes();
                        String Categoria =  hatch.getCategoria();
                        String UserEmail = hatch.getUserEmail();



                        intent.putExtra("Titulo", TextTitulo);
                        intent.putExtra("Cuerpo", TextBody);
                        intent.putExtra("Distancia", Distancia);
                        intent.putExtra("Usuario", UserNameHatch);
                        intent.putExtra("Direccion", Direccion);
                        intent.putExtra("Tipo", Tipo);
                        intent.putExtra("Id_Hatch", Id_Hatch);
                        intent.putExtra("UsuarioLogueado", username);
                        intent.putExtra("Fecha", Fecha);
                        intent.putExtra("FechaInicio", FechaInicio);
                        intent.putExtra("FechaFin", FechaFin);
                        intent.putExtra("ParticipantesRequeridos", ParticipantesRequeridos);
                        intent.putExtra("Categoria", Categoria);
                        intent.putExtra("User_Email", UserEmail);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("The read failed: " + error.getMessage());
            }


        });
    }
    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

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


    private String[] coordinateForMarker(float latitude, float longitude) {

        String[] location = new String[2];

        for (int i = 0; i <= markerLocation.size(); i++) {

            if (mapAlreadyHasMarkerForLocation((latitude + i
                    * COORDINATE_OFFSET)
                    + "," + (longitude + i * COORDINATE_OFFSET))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (mapAlreadyHasMarkerForLocation((latitude - i
                        * COORDINATE_OFFSET)
                        + "," + (longitude - i * COORDINATE_OFFSET))) {

                    continue;

                } else {
                    location[0] = latitude - (i * COORDINATE_OFFSET) + "";
                    location[1] = longitude - (i * COORDINATE_OFFSET) + "";
                    break;
                }

            } else {
                location[0] = latitude + (i * COORDINATE_OFFSET) + "";
                location[1] = longitude + (i * COORDINATE_OFFSET) + "";
                break;
            }
        }

        return location;
    }

    // Return whether marker with same location is already on map
    private boolean mapAlreadyHasMarkerForLocation(String location) {
        return (markerLocation.containsValue(location));
    }

    public class renderIcon extends DefaultClusterRenderer<Hatch>{

        public renderIcon(Context context, GoogleMap map,
                                 ClusterManager<Hatch> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Hatch item,
                                                   MarkerOptions markerOptions) {

            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);


            String[] CheckMarkersLocation = coordinateForMarker(Float.parseFloat(item.getUbicacion_lat()),Float.parseFloat(item.getUbicacion_lon()));

            LatLng FixedLocation = new LatLng(Double.parseDouble(CheckMarkersLocation[0]),Double.parseDouble(CheckMarkersLocation[1]));
            LatLng LocacionHatch = FixedLocation;


            markerOptions.position(LocacionHatch)
                    .title(item.getTitle())
                    .snippet(item.getBody());

            markerOptions.icon(BitmapDescriptorFactory.fromResource(setIcon(item.getCategoria())));

        }

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