package nupa.drafthatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

/**
 * Created by Mauro Medina on 15/07/2016.
 */
public class NuevoHatchStep1_2 extends AppCompatActivity  {


    private ImageButton catMusica, catDeportes, catSalidas, catFlash,catActivismo,catMascotas;
    private String participantesField, TitleField, DetailsField, categoria, username, categorias, userDistanceSetting, tipo;

    private int User_Id;
   private ImageView stepView;
    private Spinner participantes;
    private EditText editText;

    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();

    Thread splashTread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hatch_step1_2_drawer);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarNuevoHatch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Elige una categoria");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        editText=(EditText)findViewById(R.id.editText);
        editText.setEnabled(false);

        Intent intent = getIntent();

        TitleField= intent.getStringExtra("Titulo");
        DetailsField= intent.getStringExtra("Detalle");
         username= intent.getStringExtra("username");
        User_Id=intent.getIntExtra("User_Id", 0);
        categorias= intent.getStringExtra("Categoria");
        userDistanceSetting= intent.getStringExtra("distanceSetting");
        participantesField= intent.getStringExtra("Participantes");
        tipo= intent.getStringExtra("Tipo");



        final Animation animAlpha = AnimationUtils.loadAnimation(this,
                R.anim.alpha);
        stepView=(ImageView)findViewById(R.id.imageViewStep4);

        catMusica=(ImageButton)findViewById(R.id.imgMusica);
        catActivismo=(ImageButton)findViewById(R.id.imgActivismo);
        catDeportes=(ImageButton)findViewById(R.id.imgDeporte);
        catSalidas=(ImageButton)findViewById(R.id.imgSalidas);
        catFlash=(ImageButton)findViewById(R.id.imgFlash);
        catMascotas=(ImageButton)findViewById(R.id.imgMascotas);
        participantes=(Spinner)findViewById(R.id.spinner);




        //animation region
        (new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {

                            @Override
                            public void run()
                            {
                                Animation shake = AnimationUtils.loadAnimation(NuevoHatchStep1_2.this, R.anim.shake);

                                catMusica.startAnimation(shake);
                                catActivismo.startAnimation(shake);
                                catDeportes.startAnimation(shake);
                                catSalidas.startAnimation(shake);
                                catFlash.startAnimation(shake);
                                catMascotas.startAnimation(shake);
                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        })).start(); // the while thread will start in BG thread
        //end animation region






        //region botones categorias
        catMusica.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {

                        view.startAnimation(animAlpha);
                    catMusica.setImageResource(R.drawable.musica_icon_small_checked);
                        categorias = "Musica";

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
                        Intent mapa = new Intent(getBaseContext(), NuevoHatchStep2.class);
                mapa.putExtra("Titulo", TitleField);
                mapa.putExtra("Detalle", DetailsField);
                mapa.putExtra("Categoria", categorias);
                mapa.putExtra("username", username);
                mapa.putExtra("User_Id", User_Id);
                mapa.putExtra("distanceSetting", userDistanceSetting);
                mapa.putExtra("Participantes", participantesField);
                mapa.putExtra("Tipo", tipo);
                startActivity(mapa);

                catMascotas.setImageResource(R.drawable.mascotas_icon_small);
                catFlash.setImageResource(R.drawable.flash_icon_small);
                catSalidas.setImageResource(R.drawable.salidas_icon_small);
                catDeportes.setImageResource(R.drawable.deportes_icon_small);
                catActivismo.setImageResource(R.drawable.activismo_icon_small);

                    }


        });

        catActivismo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {

                    view.startAnimation(animAlpha);
                    catActivismo.setImageResource(R.drawable.activismo_icon_small_checked);
                    categorias = "Activismo";
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
                    Intent mapa = new Intent(getBaseContext(), NuevoHatchStep2.class);
                mapa.putExtra("Titulo", TitleField);
                mapa.putExtra("Detalle", DetailsField);
                mapa.putExtra("Categoria", categorias);
                mapa.putExtra("username", username);
                mapa.putExtra("User_Id", User_Id);
                mapa.putExtra("distanceSetting", userDistanceSetting);
                mapa.putExtra("Participantes", participantesField);
                mapa.putExtra("Tipo", tipo);
                startActivity(mapa);

                catMascotas.setImageResource(R.drawable.mascotas_icon_small);
                catFlash.setImageResource(R.drawable.flash_icon_small);
                catSalidas.setImageResource(R.drawable.salidas_icon_small);
                catDeportes.setImageResource(R.drawable.deportes_icon_small);
                catMusica.setImageResource(R.drawable.musica_icon_small);

                }

        });

        catDeportes.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {

                    view.startAnimation(animAlpha);
                    catDeportes.setImageResource(R.drawable.deportes_icon_small_checked);
                    categorias="Deportes";
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
                    Intent mapa=new Intent(getBaseContext(),NuevoHatchStep2.class);
                mapa.putExtra("Titulo", TitleField);
                mapa.putExtra("Detalle", DetailsField);
                mapa.putExtra("Categoria", categorias);
                mapa.putExtra("username", username);
                mapa.putExtra("User_Id", User_Id);
                mapa.putExtra("distanceSetting", userDistanceSetting);
                mapa.putExtra("Participantes", participantesField);
                mapa.putExtra("Tipo", tipo);
                startActivity(mapa);

                catMascotas.setImageResource(R.drawable.mascotas_icon_small);
                catFlash.setImageResource(R.drawable.flash_icon_small);
                catSalidas.setImageResource(R.drawable.salidas_icon_small);
                catActivismo.setImageResource(R.drawable.activismo_icon_small);
                catMusica.setImageResource(R.drawable.musica_icon_small);
                    }


        });

        catSalidas.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {

                    view.startAnimation(animAlpha);
                    catSalidas.setImageResource(R.drawable.salidas_icon_small_checked);
                    categorias = "Salidas";
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
                    Intent mapa = new Intent(getBaseContext(), NuevoHatchStep2.class);
                mapa.putExtra("Titulo", TitleField);
                mapa.putExtra("Detalle", DetailsField);
                mapa.putExtra("Categoria", categorias);
                mapa.putExtra("username", username);
                mapa.putExtra("User_Id", User_Id);
                mapa.putExtra("distanceSetting", userDistanceSetting);
                mapa.putExtra("Participantes", participantesField);
                mapa.putExtra("Tipo", tipo);
                startActivity(mapa);

                catMascotas.setImageResource(R.drawable.mascotas_icon_small);
                catFlash.setImageResource(R.drawable.flash_icon_small);
               catDeportes.setImageResource(R.drawable.deportes_icon_small);
                catActivismo.setImageResource(R.drawable.activismo_icon_small);
                catMusica.setImageResource(R.drawable.musica_icon_small);

                }

        });

        catFlash.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {

                    view.startAnimation(animAlpha);
                    catFlash.setImageResource(R.drawable.flash_icon_small_checked);
                    categorias = "Flash";
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
                    Intent mapa = new Intent(getBaseContext(), NuevoHatchStep2.class);
                mapa.putExtra("Titulo", TitleField);
                mapa.putExtra("Detalle", DetailsField);
                mapa.putExtra("Categoria", categorias);
                mapa.putExtra("username", username);
                mapa.putExtra("User_Id", User_Id);
                mapa.putExtra("distanceSetting", userDistanceSetting);
                mapa.putExtra("Participantes", participantesField);
                mapa.putExtra("Tipo", tipo);
                startActivity(mapa);
                catMascotas.setImageResource(R.drawable.mascotas_icon_small);
                catSalidas.setImageResource(R.drawable.salidas_icon_small);
                catDeportes.setImageResource(R.drawable.deportes_icon_small);
                catActivismo.setImageResource(R.drawable.activismo_icon_small);
                catMusica.setImageResource(R.drawable.musica_icon_small);

                }

        });

        catMascotas.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {

                    view.startAnimation(animAlpha);
                    catMascotas.setImageResource(R.drawable.mascotas_icon_small_checked);
                    categorias = "Mascotas";
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
                    Intent mapa = new Intent(getBaseContext(), NuevoHatchStep2.class);
                    mapa.putExtra("Titulo", TitleField);
                    mapa.putExtra("Detalle", DetailsField);
                    mapa.putExtra("Categoria", categorias);
                    mapa.putExtra("username", username);
                    mapa.putExtra("User_Id", User_Id);
                    mapa.putExtra("distanceSetting", userDistanceSetting);
                    mapa.putExtra("Participantes", participantesField);
                    mapa.putExtra("Tipo", tipo);
                    startActivity(mapa);


                catFlash.setImageResource(R.drawable.flash_icon_small);
                catSalidas.setImageResource(R.drawable.salidas_icon_small);
                catDeportes.setImageResource(R.drawable.deportes_icon_small);
                catActivismo.setImageResource(R.drawable.activismo_icon_small);
                catMusica.setImageResource(R.drawable.musica_icon_small);

                }

        });

        //endregion
    }

    @Override
    public void onBackPressed() {


            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_activity_drawer, menu);

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
