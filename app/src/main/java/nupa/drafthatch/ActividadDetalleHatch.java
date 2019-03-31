package nupa.drafthatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ActividadDetalleHatch extends AppCompatActivity {

   //Variables globales a la actividad

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle,UserName, detalle, direccion, titulo, tipo, participantes, usuarioLogueado, fecha, fechaInicio, fechaFin,fechaInicioFormat, fechaFinFormat, Category;
    private double distancia;
    private TextView txtUsername, txtDetalle, txtDireccion, txtDistancia, txtTipo, txtParticipantes,txtFecha,txtCategory;
    static int id_hatch, participantesRequeridos;
    private Button btnUnirse,btnNotify, btnCancelar;
    private String registration_id=null;
    private int User_id;
//ESTANDAR
    private String UserNameHatch;
    private String OwnerIDHatch;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    static boolean flag = false;
    static boolean hatchCompleto;

    static int countParticipantes=0;
    static int countParticipantesText=0;
    //NEW LOGIN FIREBASE
    private static final String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private Notificaciones notify;
    private ImageView imageViewIcon;

    static String nameFacebook;
    static String nameFirebase;

    //END NEW LOGIN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_arround);

        notify = new Notificaciones(getApplicationContext());

        Intent intent=getIntent();

        UserName = intent.getStringExtra("User_Name");
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting = intent.getStringExtra("categories");
        UserNameHatch=intent.getStringExtra("Usuario"); //B
        detalle=intent.getStringExtra("Cuerpo");
        direccion=intent.getStringExtra("Direccion");
        titulo=intent.getStringExtra("Titulo");
        distancia=intent.getDoubleExtra("Distancia",0.0);
        tipo=intent.getStringExtra("Tipo");
        id_hatch=intent.getIntExtra("Id_Hatch",0);
        fecha=intent.getStringExtra("Fecha");
        fechaInicio=intent.getStringExtra("FechaInicio");
        fechaFin=intent.getStringExtra("FechaFin");
        participantesRequeridos=intent.getIntExtra("ParticipantesRequeridos",0);
        OwnerIDHatch=intent.getStringExtra("OwnerIDHatch");
        Category=intent.getStringExtra("Categoria");


        imageViewIcon=(ImageView)findViewById(R.id.txtImagenCategoria);

        if (Category.equals("Musica")) {
            imageViewIcon.setImageResource(R.drawable.musica_icon_small);

        }
        if (Category.equals("Activismo")) {
            imageViewIcon.setImageResource(R.drawable.activismo_icon_small);
        }
        if (Category.equals("Deportes")) {
            imageViewIcon.setImageResource(R.drawable.deportes_icon_small);
        }
        if (Category.equals("Salidas")) {
            imageViewIcon.setImageResource(R.drawable.salidas_icon_small);
        }
        if (Category.equals("Flash")) {
            imageViewIcon.setImageResource(R.drawable.flash_icon_small);
        }
        if (Category.equals("Mascotas")) {
            imageViewIcon.setImageResource(R.drawable.mascotas_icon_small);
        }

        txtUsername=(TextView)findViewById(R.id.txtUsuarioCreador);
        txtUsername.setText(UserNameHatch);

        txtDetalle=(TextView)findViewById(R.id.txtDescripcion);
        txtDetalle.setText(detalle);

        txtDetalle.setLinksClickable(true);
        txtDetalle.setAutoLinkMask(Linkify.WEB_URLS);
        txtDetalle.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(txtDetalle, Linkify.WEB_URLS);

        txtDireccion=(TextView)findViewById(R.id.txtDireccion);
        txtDireccion.setText(direccion);
        txtDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DetalleAlMapa= new Intent(getBaseContext(),DetalleHatchModoMapa.class);
                DetalleAlMapa.putExtra("username", UserName);
                DetalleAlMapa.putExtra("distanceSetting", userDistanceSetting);
                DetalleAlMapa.putExtra("categories", userCategoriesSetting);
                DetalleAlMapa.putExtra("User_Id", User_id);
                DetalleAlMapa.putExtra("Id_Hatch", id_hatch);
                startActivity(DetalleAlMapa);
            }
        });

        txtTipo=(TextView)findViewById(R.id.txtTipo);
        txtTipo.setText(tipo);

        btnUnirse=(Button)findViewById(R.id.btnConfirmar);

        ImageView imgtipo = (ImageView)findViewById(R.id.imgTipo);
        if (tipo.equals("Abierto")){
            imgtipo.setImageResource(R.drawable.ic_hatch_open);
        }else{
            imgtipo.setImageResource(R.drawable.ic_hatch_closed);
        }


        txtParticipantes=(TextView)findViewById(R.id.txtParticipantes);

        txtFecha=(TextView)findViewById(R.id.txtFecha);
       // fechaInicioFormat= fechaInicio.substring(0,10)+"-"+fechaInicio.substring(5,7)+ " " +fechaInicio.substring(11,16);
        //fechaFinFormat= fechaFin.substring(8,10)+"-"+fechaFin.substring(5,7)+ " " +fechaFin.substring(11,16);
        fechaInicioFormat = fechaInicio.substring(0,5) +" " + fechaInicio.substring(11, 16);
        fechaFinFormat = fechaFin.substring(0,5) +" " + fechaFin.substring(11, 16);

// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // ...
       /* mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //nameFacebook = user.getDisplayName();
                    nameFacebook = mAuth.getCurrentUser().getDisplayName();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // ...*/
        nameFacebook = mAuth.getCurrentUser().getDisplayName();



        if (nameFacebook==null || nameFacebook.equals("")){
            DatabaseReference getUserName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
            getUserName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String user_name = dataSnapshot.child("name").getValue().toString();
                    String user_lastName = dataSnapshot.child("apellido").getValue().toString();

                    nameFirebase = user_name + " " + user_lastName;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        txtFecha.setText(fechaInicioFormat +" - "+fechaFinFormat);


        double distanciaM, distanciaKm;
        String distanciaBruta,distanciaMostrar,distanciaMostrarKm;

        distanciaM=distancia;
        distanciaKm=distanciaM*0.001;

        distanciaMostrarKm=Double.toString(distanciaKm);

        distanciaBruta=Double.toString(distancia);
        distanciaMostrar=distanciaBruta.substring(0,distanciaBruta.indexOf("."));
        txtDistancia=(TextView)findViewById(R.id.txtDistancia);


        if (distanciaMostrar.length()<=3){
            txtDistancia.setText(distanciaMostrar+ " mts");
        }else{
            txtDistancia.setText(distanciaMostrarKm.substring(0,distanciaMostrarKm.indexOf("."))+ " kms");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNuevoHatch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titulo);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        clearParticipantesCounterText(0);
        getHatchParticipantes(id_hatch);

        if (btnUnirse.getText().equals("Calculando...")) {
            ChequearSiEsParticipanteoDueño(id_hatch);
        }


        btnUnirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnUnirse.getText().equals("Unirse")) {

                        DatabaseReference mDatabaseParticipantes = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(id_hatch));
                        mDatabaseParticipantes.child("HatchId").setValue(String.valueOf(id_hatch));
                        mDatabaseParticipantes.child("Estado").setValue(tipo);
                        mDatabaseParticipantes.child("Users").child(mAuth.getCurrentUser().getUid().toString()).setValue(mAuth.getCurrentUser().getUid().toString());
                        Toast.makeText(getApplicationContext(), "Se ha unido al Evento!!!", Toast.LENGTH_LONG).show();
                        sendNotificaction("Union");

                        closeHatchIfFull(id_hatch);

                        Intent intent = new Intent(ActividadDetalleHatch.this, MainScreen.class);
                        startActivity(intent);
                        finish();
                    }else{

                        //MENSAJE DE HATCH FULL
                       // Toast.makeText(getApplicationContext(), "No hay cupos para participar!", Toast.LENGTH_LONG).show();
                    }

                   /* parametros.add(new BasicNameValuePair("Username", usuarioLogueado));
                    parametros.add(new BasicNameValuePair("Id_Hatch", String.valueOf(id_hatch)));

                    AsyncCall task = new AsyncCall();
                    task.execute();*/

                if (btnUnirse.getText().equals("Ir al Mapa!")) {
                //Crear copia MAP Fragment y averiguar como hacer foco en la ubicacion
                }

                if (btnUnirse.getText().equals("Cancelar")|| btnUnirse.getText().equals("Darse de Baja")) {

                    //DARSE DE BAJA COMO PARTICIPANTE o COMO DUEÑO
                    CancelarHatch(id_hatch);
                    Intent intent = new Intent(ActividadDetalleHatch.this, MainScreen.class);
                    startActivity(intent);
                    finish();
                }
                if (btnUnirse.getText().equals("Unido")) {
                    btnUnirse.setEnabled(false);

                }

            }
        });
       /* btnCancelar=(Button)findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parametros.add(new BasicNameValuePair("Username",usuarioLogueado));
                parametros.add(new BasicNameValuePair("Id_Hatch",String.valueOf(id_hatch)));

                AsyncCall task=new AsyncCall();
                task.execute();
            }
        });*/


//OTHERPROFILE INFO QUEDA PARA MAS ADELANTE, COMENTO

        txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserName.equals(mAuth.getCurrentUser().getDisplayName()))
                {

                }else {
                    Intent intentProfile = new Intent(getBaseContext(), OtherProfileActivity.class);
                    intentProfile.putExtra("distanceSetting", userDistanceSetting);
                    intentProfile.putExtra("categories", userCategoriesSetting);
                    intentProfile.putExtra("User_Id", User_id);
                    intentProfile.putExtra("username", UserName);//MIRANDO OTRO PERFIL
                    startActivity(intentProfile);
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_drawer, menu);

        return true;
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





    public void getHatchParticipantes(int idhatch) {
        {

            DatabaseReference getCantidadParticipantes = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(idhatch)).child("Users");

            getCantidadParticipantes.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        incrementParticipantesCounterText(1);
                    }

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });

            DatabaseReference getHatches = FirebaseDatabase.getInstance().getReference().child("hatchs").child(String.valueOf(idhatch));
            getHatches.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String hatchparticipantes = (String) dataSnapshot.child("Participantes").getValue();
                    String hatchTipo = (String) dataSnapshot.child("Tipo").getValue();
                    if (hatchTipo.equals("Cerrado")){
                        txtParticipantes.setText(String.valueOf(countParticipantesText) +" de "+hatchparticipantes  );
                    }
                    else{
                        txtParticipantes.setText(String.valueOf(countParticipantesText));
                    }
                    if (countParticipantesText>=Integer.parseInt(hatchparticipantes)){

                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
 }
    public void closeHatchIfFull(int idhatch) {
        {
            clearParticipantesCounterText(0);
            DatabaseReference getCantidadParticipantes = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(idhatch)).child("Users");

            getCantidadParticipantes.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        incrementParticipantesCounterText(1);
                    }

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });

            final DatabaseReference getHatches = FirebaseDatabase.getInstance().getReference().child("hatchs").child(String.valueOf(idhatch));
            getHatches.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String hatchparticipantes = (String) dataSnapshot.child("Participantes").getValue();

                    if (countParticipantesText>=Integer.parseInt(hatchparticipantes)){
                        getHatches.child("Status").setValue("Cerrado");
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    public void ChequearSiEsParticipanteoDueño(final int idhatch) {
        {

            DatabaseReference SiEsDueñoDelHatch = FirebaseDatabase.getInstance().getReference().child("hatchs").child(String.valueOf(idhatch)).child("User_Id");
            SiEsDueñoDelHatch.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String campoUserID = dataSnapshot.getValue(String.class);

                    if(campoUserID.equals(mAuth.getCurrentUser().getUid().toString())){
                        //SOY CREADOR DEL HATCH
                        btnUnirse.setText("Cancelar");
                    }
                    else{
                        //BUSCO SI SOY PARTICIPANTE

                        DatabaseReference LeerParticipantes = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(idhatch)).child("Users");
                        LeerParticipantes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean EsParticipante = false;
                                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                    if(messageSnapshot.getValue().equals(mAuth.getCurrentUser().getUid().toString())){
                                        btnUnirse.setText("Darse de Baja");
                                        EsParticipante = true;
                                    }
                                }

                                //String noEsParticipante = dataSnapshot.child().getValue(String.class);

                                if (!EsParticipante) {
                                    btnUnirse.setText("Unirse");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                System.out.println("The read failed: " + error.getMessage());
                            }
                        });

                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });

        }
    }

    public void CancelarHatch(final int idhatch) {
        {

            DatabaseReference SiEsDueñoDelHatch = FirebaseDatabase.getInstance().getReference().child("hatchs").child(String.valueOf(idhatch)).child("User_Id");
            SiEsDueñoDelHatch.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String campoUserID = dataSnapshot.getValue(String.class);

                        if(campoUserID.equals(mAuth.getCurrentUser().getUid().toString())){
                            //SOY CREADOR DEL HATCH
                            DatabaseReference LeerParticipantes = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(idhatch)).child("Users");
                            LeerParticipantes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                        if(!(messageSnapshot.getValue().equals(mAuth.getCurrentUser().getUid().toString()))){
                                            messageSnapshot.getRef().removeValue();

                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    System.out.println("The read failed: " + error.getMessage());
                                }
                            });

                            DatabaseReference CerrarHatch = FirebaseDatabase.getInstance().getReference().child("hatchs").child(String.valueOf(idhatch));
                            CerrarHatch.child("Status").setValue("Cerrado");

                           final DatabaseReference CerrarParticipacion = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(idhatch));
                            CerrarParticipacion.child("Estado").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String estadoParticipacion = dataSnapshot.getValue(String.class);
                                    if (!(TextUtils.isEmpty(estadoParticipacion))){
                                        CerrarParticipacion.child("Estado").setValue("Cerrado");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(getApplicationContext(), "Se ha Cancelado su Evento", Toast.LENGTH_LONG).show();
                            sendNotificaction("CancelacionCreador");
                        }
                    else{
                            //SOY PARTICIPANTE
                            DatabaseReference LeerParticipantes = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(idhatch)).child("Users");
                            LeerParticipantes.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                        if(messageSnapshot.getValue().equals(mAuth.getCurrentUser().getUid().toString())){
                                            messageSnapshot.getRef().removeValue();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    System.out.println("The read failed: " + error.getMessage());
                                }
                            });
                            sendNotificaction("CancelacionUsuario");
                            Toast.makeText(getApplicationContext(), "Te has salido del Evento", Toast.LENGTH_LONG).show();
                        }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("The read failed: " + error.getMessage());
                }
            });

        }
    }



    public void incrementParticipantesCounterText(int a){countParticipantesText=countParticipantesText+a; }
    public void clearParticipantesCounterText(int a){countParticipantesText=a; }
    public void setTrue(boolean a){a = true; }
    public void setFalse(boolean a){a = false; }



    public void sendNotificaction(final String accion) {


        if (accion != "CancelacionCreador") {
            //  OBTENGO REGISTRATION ID  Y ENVIO NOTIFICACION
            DatabaseReference getRegistrationID = FirebaseDatabase.getInstance().getReference().child("users").child(OwnerIDHatch);
            getRegistrationID.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //String registrationID = dataSnapshot.getValue(String.class);
                    String registrationID = dataSnapshot.child("registration_id").getValue().toString();

                    String flag_notification = dataSnapshot.child("flag_notification").getValue().toString();

                    if (flag_notification.equals("1")){
                        if (nameFacebook != null){
                            notify.sendNotification(registrationID, accion, nameFacebook, titulo);
                        }else {
                            notify.sendNotification(registrationID, accion, nameFirebase , titulo);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //
        } else {

            DatabaseReference getRegistrationIDs = FirebaseDatabase.getInstance().getReference().child("participantes").child(String.valueOf(id_hatch)).child("Users");

            final List<String> userIDs = new ArrayList<>();

            getRegistrationIDs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        String userID = (String) messageSnapshot.getValue();
                        userIDs.add(userID);

                        DatabaseReference getRegistrationID = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("registration_id");
                        getRegistrationID.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String registrationID = dataSnapshot.getValue(String.class);
                                notify.sendNotification(registrationID, accion, UserNameHatch, titulo);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

}

