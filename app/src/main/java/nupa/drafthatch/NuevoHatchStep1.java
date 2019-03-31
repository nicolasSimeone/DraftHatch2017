package nupa.drafthatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Map;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by Mauro Medina on 15/07/2016.
 */
public class NuevoHatchStep1  extends AppCompatActivity {

    private TextView NameEvent, DescriptionEvent, Atendees, MaxUsers;
    private EditText Name, Description;

    private String username;
    private int User_Id;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    private ImageView ImgUnlocked, ImgLocked;
    private Spinner participantes;
    private String[] numneroParticipantes= new String[31];
    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
    private String tipo="Abierto";
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static String name;
    static String email;
    static Uri photoUrl;
    static String uid;
    private Boolean flagTouchTitle = false;
    private Boolean flagTouchDescription = false;



    Thread splashTread;

    //FIREBASE

    private int CAMERA_REQUEST_CODE = 0;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    public ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hatch_step1);

        for (int i=0;i<31;i++){
            numneroParticipantes[i]=String.valueOf(i+1);
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarNuevoHatch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crea un Evento!");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //FIREBASE
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // ...

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl= user.getPhotoUrl();
            uid = user.getUid();

        }
        else{
            Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(intent);
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator );
                userDistanceSetting = map.get("distance");
                userCategoriesSetting= map.get("categories");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //END FIREBASE
        Intent intent=getIntent();

        username= uid;
        User_Id=intent.getIntExtra("User_Id", 0);


        final Animation animAlpha = AnimationUtils.loadAnimation(this,
                R.anim.alpha);




        ImgUnlocked=(ImageView)findViewById(R.id.ImgUnlocked);
        ImgLocked=(ImageView)findViewById(R.id.ImgLocked);


        participantes=(Spinner)findViewById(R.id.spinner);

        Name=(EditText)findViewById(R.id.editText1);
        Description=(EditText)findViewById(R.id.editText2);

        NameEvent=(TextView)findViewById(R.id.TxtView1);
        DescriptionEvent=(TextView)findViewById(R.id.textView9);
        Description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Linkify.addLinks(editable, Linkify.WEB_URLS);
            }
        });

        Atendees=(TextView)findViewById(R.id.textView11);
        MaxUsers=(TextView)findViewById(R.id.textView5);
        MaxUsers.setVisibility(View.INVISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, numneroParticipantes);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        Name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !flagTouchTitle){
                    Name.setText("");
                    flagTouchTitle=true;
                }
                return false;
            }
        });
       Description.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN &&!flagTouchDescription) {
                    Description.setText("");
                    flagTouchDescription=true;
                }
                return false;
            }
        });
        participantes.setAdapter(new NothingSelectedSpinnerAdapter(adapter,R.layout.spinner_participantes,getBaseContext()));
        participantes.setPrompt("Atendees");
        participantes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    participantes.performClick();
                }

                return false;
            }
        });



        participantes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(adapterView.getItemAtPosition(i) !=null){
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if (!(selectedItem.equals("") || selectedItem==null)) {
                    // do your stuff

                    Intent mapa = new Intent(getBaseContext(), NuevoHatchStep1_2.class);
                    mapa.putExtra("Titulo", Name.getText().toString());
                    mapa.putExtra("Detalle", Description.getText().toString());
                    mapa.putExtra("Categoria", "TBD");
                    mapa.putExtra("username", username);
                    mapa.putExtra("User_Id", User_Id);
                    mapa.putExtra("distanceSetting", userDistanceSetting);
                    mapa.putExtra("Participantes", participantes.getSelectedItem().toString());
                    mapa.putExtra("Tipo", "Cerrado");
                    startActivity(mapa);
                }
            }}

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        participantes.setVisibility(View.INVISIBLE);

                ImgUnlocked.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {

                        if (Name.getText().toString().equals("") || Name.getText().toString().equals("Escriba el titulo de su evento") || Description.getText().toString().equals("Escriba una descripción") || Description.getText().toString().equals("") || (tipo.equals("Cerrado") && (participantes.getSelectedItem() == null))) {
                            if (tipo.equals("Cerrado") && (participantes.getSelectedItem() == null)) {

                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatchStep1.this);
                                dialogBuilder.setMessage("El numero de participantes es requerido");
                                dialogBuilder.setPositiveButton("Ok", null);
                                dialogBuilder.show();
                            }
                            if (Description.getText().toString().equals("Escriba una descripción") || Description.getText().toString().equals("")) {

                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatchStep1.this);
                                dialogBuilder.setMessage("Por favor escriba un detalle");
                                dialogBuilder.setPositiveButton("Ok", null);
                                dialogBuilder.show();
                            }
                            if (Name.getText().toString().equals("") || Name.getText().toString().equals("Escriba el titulo de su evento")) {

                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatchStep1.this);
                                dialogBuilder.setMessage("Por favor escriba un titulo");
                                dialogBuilder.setPositiveButton("Ok", null);
                                dialogBuilder.show();
                            }

                        } else {
                            participantes.setVisibility(View.INVISIBLE);
                            ImgUnlocked.setImageResource(R.drawable.unlocked_icon_checked);
                            ImgLocked.setImageResource(R.drawable.unlocked_icon);

                            splashTread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        int waited = 0;
                                        // Splash screen pause time
                                        while (waited < 2000) {
                                            sleep(100);
                                            waited += 100;
                                        }

                                    } catch (InterruptedException e) {
                                        // do nothing
                                    } finally {

                                    }

                                }
                            };
                            splashTread.start();

                            Intent mapa = new Intent(getBaseContext(), NuevoHatchStep1_2.class);
                            mapa.putExtra("Titulo", Name.getText().toString());
                            mapa.putExtra("Detalle", Description.getText().toString());
                            mapa.putExtra("Categoria", "TBD");
                            mapa.putExtra("username", username);
                            mapa.putExtra("User_Id", User_Id);
                            mapa.putExtra("distanceSetting", userDistanceSetting);
                            mapa.putExtra("Participantes", "99999");
                            mapa.putExtra("Tipo", "Abierto");
                            startActivity(mapa);
                           // ImgUnlocked.setImageResource(R.drawable.unlocked_icon);

                        }
                    }


                });

        ImgLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Name.getText().toString().equals("") || Name.getText().toString().equals("Type a Title for your Event") || Description.getText().toString().equals("Type a Description") || Description.getText().toString().equals("")|| ( tipo.equals("Cerrado") && (participantes.getSelectedItem() ==null))) {
                    if ( tipo.equals("Cerrado") && (participantes.getSelectedItem() ==null)) {

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatchStep1.this);
                        dialogBuilder.setMessage("Number of participants are required before proceeding");
                        dialogBuilder.setPositiveButton("Ok", null);
                        dialogBuilder.show();
                    }
                    if (Description.getText().toString().equals("Type a Description") || Description.getText().toString().equals("")) {

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatchStep1.this);
                        dialogBuilder.setMessage("Details are required before proceeding");
                        dialogBuilder.setPositiveButton("Ok", null);
                        dialogBuilder.show();
                    }
                    if (Name.getText().toString().equals("") || Name.getText().toString().equals("Type a Title for your Event")) {

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatchStep1.this);
                        dialogBuilder.setMessage("Title is required before proceeding");
                        dialogBuilder.setPositiveButton("Ok", null);
                        dialogBuilder.show();
                    }

                }else{
                    ImgUnlocked.setImageResource(R.drawable.unlocked_icon);
                    ImgLocked.setImageResource(R.drawable.unlocked_icon__checked);
                MaxUsers.setVisibility(View.VISIBLE);
                participantes.setVisibility(View.VISIBLE);

                participantes.performClick();

            }}
        });




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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


}
