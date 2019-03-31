package nupa.drafthatch;


import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;


public class HatchesJoinedFragment extends Fragment{


    //region Variables
    private String[] stringArray;  //Opciones del menu
    private ArrayAdapter itemArrayAdapter;
    public ArrayList<Hatch> Hatchs = new ArrayList<Hatch>();  //Lista de Hatchs del newsfeed
    private TextView Cargando,usernameMenu;  //Text "Cargando"mientras carga el newsfeed
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    public  ListView listaHatchs;
    private LatLng latLng;
    private Geocoder geocoder;
    private List<Address> addresses=null;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double distancia;
    private String username;
    private int User_Id;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    private SwipeRefreshLayout refreshLayout;
    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
    Location locacionUserPuntoA=new Location("PuntoA");

    double distanciaM, distanciaKm;
    static String CategoriesList ;
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static String name;
    static String email;
    static Uri photoUrl;
    static String uid;

    //endregion
    public HatchesJoinedFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_hatches_joined, container, false);


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
                    Intent intent = new Intent(getApplicationContext(), loginMenu.class);
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
        if (user != null) {

            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl = user.getPhotoUrl();
            uid = user.getUid();
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                userDistanceSetting = map.get("distance");
                userCategoriesSetting = map.get("categories");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        username = user.getUid();
        User_Id = getActivity().getIntent().getIntExtra("User_Id", 0);
        locacionUserPuntoA = ((HatchesNotificationActivity) getActivity()).locacionUserPuntoA;  //El manejo del GPS lo hago hago en la actividad que contiene los fragments


        refreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout_joined);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLayout.setRefreshing(true);

                Hatchs.clear();
                listHatches();
            }});

        listHatches();

        return view;

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();



    }

        public void listHatches() {

            final List<String>participantes = new ArrayList<String>();

            DatabaseReference getParticipantes = FirebaseDatabase.getInstance().getReference().child("participantes");
            getParticipantes.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot2 : dataSnapshot.getChildren()) {
                        String participanteEstado = (String) messageSnapshot2.child("Estado").getValue();
                         String participanteHatchId = (String) messageSnapshot2.child("HatchId").getValue();
                        final String participanteHatchIdFinal = (String) messageSnapshot2.child("HatchId").getValue();


                        for (DataSnapshot messageSnapshot4 : dataSnapshot.child(participanteHatchId).child("Users").getChildren()) {
                            String participanteUserId = (String) messageSnapshot4.getValue();
                            if(participanteUserId.equals(mAuth.getCurrentUser().getUid().toString())) {
                                participantes.add(participanteHatchIdFinal);
                            }
                        }

             }

        }

    @Override
     public void onCancelled(DatabaseError databaseError) {

        }});


            DatabaseReference getHatches = FirebaseDatabase.getInstance().getReference().child("hatchs");
            getHatches.addListenerForSingleValueEvent(new ValueEventListener() {


                // final ArrayList<Hatch>output = new ArrayList<Hatch>();

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<Hatch>output = new ArrayList<Hatch>();
                    itemArrayAdapter = new Adapter(getActivity(), output);
                    listaHatchs = (ListView) getActivity().findViewById(R.id.ListaHatchJoined);
                    listaHatchs.setAdapter(itemArrayAdapter);

                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        final String hatchID = (String) messageSnapshot.child("HatchID").getValue();
                        final String hatchTitulo = (String) messageSnapshot.child("Titulo").getValue();
                        final String hatchEstado = (String) messageSnapshot.child("Status").getValue();
                        final String hatchBody = (String) messageSnapshot.child("Cuerpo").getValue();
                        final String hatchLat = (String) messageSnapshot.child("Latitud").getValue();
                        final String hatchLng = (String) messageSnapshot.child("Longitud").getValue();
                        final String hatchusername = (String) messageSnapshot.child("User_Name").getValue();
                        final String hatchdireccion = (String) messageSnapshot.child("Direccion").getValue();
                        final String hatchtipo = (String) messageSnapshot.child("Tipo").getValue();
                        final String hatchparticipantes = (String) messageSnapshot.child("Participantes").getValue();
                        final String hatchfechainicio = (String) messageSnapshot.child("FechaInicio").getValue();
                        final String hatchfechafin = (String) messageSnapshot.child("FechaFin").getValue();
                        String hCategoria = (String) messageSnapshot.child("Categoria").getValue();
                        String hUserId = (String) messageSnapshot.child("User_Id").getValue();
                        String hUserEmail = (String)messageSnapshot.child("User_Email").getValue();
                        Long hEpochStart = (Long)messageSnapshot.child("EpochStart").getValue();
                        Long hEpochEnd = (Long)messageSnapshot.child("EpochFin").getValue();

                        CategoriesList = userCategoriesSetting;

                        Location locacionUserPuntoB = new Location("PuntoB");
                        locacionUserPuntoB.setLatitude(Double.parseDouble(hatchLat));
                        locacionUserPuntoB.setLongitude(Double.parseDouble(hatchLng));

                        double Distance = locacionUserPuntoA.distanceTo(locacionUserPuntoB);


                        for (int i=0;i<participantes.size();i++) {
                            if (participantes.get(i).equals(hatchID)) {
                                Hatch h = new Hatch(hatchID, hatchTitulo, hatchBody, hatchLat, hatchLng, hatchusername, hatchdireccion, hatchtipo, Integer.parseInt(hatchID), Integer.parseInt(hatchparticipantes), hatchfechainicio, hatchfechafin, hatchEstado, Distance, hCategoria,hUserId, hUserEmail, hEpochStart, hEpochEnd);
                                output.add(h);
                                itemArrayAdapter.notifyDataSetChanged();
                            }
                        }

                        Collections.sort(output, new Comparator<Hatch>() {
                            @Override
                            public int compare(Hatch hatch, Hatch h1) {
                                return Long.valueOf(hatch.getEpochStart()).compareTo(h1.getEpochStart());
                            }
                        });
                        //ESTO es para poner la imagen de fondo si no hay hatches
                        /*if(participantes.size()==0){
                            listaHatchs.setBackgroundColor(Color.RED);
                        }else {
                            listaHatchs.setBackgroundColor(Color.WHITE);
                        }*/

                        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //Click en el detalle del hatch

                                Intent intent = new Intent(getActivity().getApplicationContext(), ActividadDetalleHatch.class);
                                Bundle b = new Bundle();
                                String TextTitulo = output.get(position).getTitle();
                                String TextBody = output.get(position).getBody();
                                Double Distancia = output.get(position).getDistancia();
                                String UserNameHatch = output.get(position).getUsername();
                                String Direccion = output.get(position).getDireccion();
                                String Tipo = output.get(position).getTipo();
                                int Id_Hatch = output.get(position).getId_Hatch();
                                String Fecha = output.get(position).getFecha();
                                String FechaInicio = output.get(position).getFechaInicio();
                                String FechaFin = output.get(position).getFechaFin();
                                String OwnerIDH = output.get(position).getOwnerIDHatch();
                                int ParticipantesRequeridos = output.get(position).getParticipantes();
                                String Categoria = output.get(position).getCategoria();
                                String UserEmail = output.get(position).getUserEmail();


                                intent.putExtra("Titulo", TextTitulo);
                                intent.putExtra("Cuerpo", TextBody);
                                intent.putExtra("Distancia", Distancia);
                                intent.putExtra("Usuario", UserNameHatch);//B
                                intent.putExtra("Direccion", Direccion);
                                intent.putExtra("Tipo", Tipo);
                                intent.putExtra("Id_Hatch", Id_Hatch);
                                intent.putExtra("UsuarioLogueado", username);//A
                                intent.putExtra("Fecha", Fecha);
                                intent.putExtra("FechaInicio", FechaInicio);
                                intent.putExtra("FechaFin", FechaFin);
                                intent.putExtra("ParticipantesRequeridos", ParticipantesRequeridos);
                                intent.putExtra("OwnerIDHatch", OwnerIDH);
                                intent.putExtra("Categoria", Categoria);
                                intent.putExtra("User_Email", UserEmail);

                                startActivity(intent);

                            }
                        };
                        listaHatchs.setOnItemClickListener(listener); // adjunto listener a la lista de Hatchs cuando se hace click
                        refreshLayout.setRefreshing(false);


                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });


        }
}
