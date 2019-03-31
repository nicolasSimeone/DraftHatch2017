package nupa.drafthatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NuevoHatchStep4 extends AppCompatActivity {

    private int User_Id;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    static String username;
    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();

    private TextView txtTitle, txtDetails, txtMaxParticipantes, txtFecha, txtFechaEnd, txtDireccion, txtCategory;
    private String TitleField, DetailsField, categorias, tipo, participantes, Latitud, Longitud, UserName;
    private String Direccion;
    private String FechaStart, FechaEnd;
    static ImageView imageViewStep4, HatchImage, imageViewIcon;

    private double epochStart, epochEnd;

    //CALCULO DISTANCIA PARA FIREBASE
    double distancia=100;

    Location locacionUser=new Location("PuntoA");


       //


    private Button btnConfirmar;
    private Button btnCancelar;
    private Bitmap bmp;


    //NEW LOGIN FIREBASE
    private static final String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    //END NEW LOGIN
    static int HatchID = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nuevo_hatch_step4);
        Intent intent=getIntent();

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarNuevoHatch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todo listo?");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

        username= intent.getStringExtra("username");
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting=intent.getStringExtra("categories");
        User_Id=intent.getIntExtra("User_Id", 0);

        TitleField=intent.getStringExtra("Titulo");
        DetailsField=intent.getStringExtra("Detalle");
        tipo=intent.getStringExtra("Tipo");
        participantes=intent.getStringExtra("Participantes");
        categorias=intent.getStringExtra("Categoria");

        Latitud=intent.getStringExtra("Latitud");
        Longitud=intent.getStringExtra("Longitud");
        Direccion=intent.getStringExtra("Direccion");

        FechaStart=intent.getStringExtra("fechaStart");
        FechaEnd=intent.getStringExtra("fechaEnd");

        epochStart = intent.getDoubleExtra("epochStart",0);
        epochEnd = intent.getDoubleExtra("epochEnd",0);
        //   bmp =intent.getParcelableExtra("Bitmap");


        txtTitle=(TextView)findViewById(R.id.textUsername);
        txtDetails=(TextView)findViewById(R.id.txtDetalle);
        txtMaxParticipantes=(TextView)findViewById(R.id.Participantes);
        txtFecha=(TextView)findViewById(R.id.txtFecha);
        txtFechaEnd=(TextView)findViewById(R.id.textFechaEnd);
        txtDireccion=(TextView)findViewById(R.id.txtDireccion);

        btnConfirmar=(Button)findViewById(R.id.btnConfirmar);
        //btnCancelar=(Button)findViewById(R.id.button6);
        imageViewStep4=(ImageView)findViewById(R.id.imageViewStep4);
        imageViewIcon=(ImageView)findViewById(R.id.imageViewIcon);
        if (categorias.equals("Musica")) {
            imageViewIcon.setImageResource(R.drawable.musica_icon_big);

        }
        if (categorias.equals("Activismo")) {
            imageViewIcon.setImageResource(R.drawable.activismo_icon_big);
        }
        if (categorias.equals("Deportes")) {
            imageViewIcon.setImageResource(R.drawable.deportes_icon_big);
        }
        if (categorias.equals("Salidas")) {
            imageViewIcon.setImageResource(R.drawable.salidas_icon_big);
        }
        if (categorias.equals("Flash")) {
            imageViewIcon.setImageResource(R.drawable.flash_icon_big);
        }
        if (categorias.equals("Mascotas")) {
            imageViewIcon.setImageResource(R.drawable.mascotas_icon_big);
        }




        txtTitle.setText(TitleField);
        txtDetails.setText(DetailsField);
       // txtCategories.setText(categorias);
        if(tipo.equals("Abierto")) {
            txtMaxParticipantes.setText("Sin limite de Participantes");
        }
        if(tipo.equals("Cerrado")) {
            txtMaxParticipantes.setText( "Maximo Participantes: "+participantes);
        }


        txtFecha.setText(FechaStart);
        txtFechaEnd.setText(FechaEnd);

        txtDireccion.setText(Direccion);

      // CALCULO DISTANCIA PARA FIREBASE

        Location locacionUserPuntoB=new Location("PuntoB");
        locacionUserPuntoB.setLatitude(Double.parseDouble(Latitud));
        locacionUserPuntoB.setLongitude(Double.parseDouble(Longitud));

        distancia=locacionUser.distanceTo(locacionUserPuntoB);

       final  DatabaseReference getHatchID = FirebaseDatabase.getInstance().getReference().child("settings").child("HatchID");
        getHatchID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = dataSnapshot.getValue(String.class);
                if(message ==null){
                    getHatchID.setValue("0");
                }
               else{

                    HatchID=Integer.parseInt(message);
                    HatchID=HatchID+1;
                    getHatchID.setValue(String.valueOf(HatchID));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("hatchs");
                final DatabaseReference currentUserDB = mDatabase.child(String.valueOf(HatchID));

                currentUserDB.child("HatchID").setValue(String.valueOf(HatchID));
                currentUserDB.child("Categoria").setValue(categorias);
                currentUserDB.child("Titulo").setValue(TitleField);
                currentUserDB.child("Latitud").setValue(Latitud);
                currentUserDB.child("Longitud").setValue(Longitud);
                currentUserDB.child("Cuerpo").setValue(DetailsField);
                currentUserDB.child("User_Id").setValue(username);
                currentUserDB.child("User_Email").setValue(mAuth.getCurrentUser().getEmail());
                currentUserDB.child("Direccion").setValue(Direccion);
                currentUserDB.child("Tipo").setValue(tipo);
                currentUserDB.child("FechaInicio").setValue(FechaStart);
                currentUserDB.child("FechaFin").setValue(FechaEnd);
                currentUserDB.child("Participantes").setValue(participantes);
                currentUserDB.child("Status").setValue("Abierto");
                currentUserDB.child("Imagen").setValue("default");
                currentUserDB.child("Distancia").setValue(String.valueOf(distancia));
                currentUserDB.child("EpochStart").setValue(epochStart);
                currentUserDB.child("EpochFin").setValue(epochEnd);

                if (mAuth.getCurrentUser().getDisplayName() == null) {
                    DatabaseReference getUserName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("name");
                    getUserName.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String user_name = dataSnapshot.getValue(String.class);
                            currentUserDB.child("User_Name").setValue(user_name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    currentUserDB.child("User_Name").setValue(mAuth.getCurrentUser().getDisplayName());
                }

                Toast.makeText(getApplicationContext(), "Su Evento fue creado!!!", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(),MainScreen.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
}




