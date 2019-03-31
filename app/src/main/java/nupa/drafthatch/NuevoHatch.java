package nupa.drafthatch;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import java.util.ArrayList;


public class NuevoHatch extends AppCompatActivity  {

    private String mActivityTitle;
    private EditText titulo, Cuerpos;
    private ImageButton catMusica, catDeportes, catSalidas, catFlash,catActivismo,catMascotas;
    private String categorias;
    private String username;
    private int User_Id;
    static String userDistanceSetting;
    static String userCategoriesSetting;

    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hatch_drawer);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarNuevoHatch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        titulo=(EditText)findViewById(R.id.txtTitulo);
        titulo.setText("Insert Title Here!");
        titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titulo.setText("");
            }
        });

        Intent intent=getIntent();
        username= intent.getStringExtra("username");
        User_Id=intent.getIntExtra("User_Id", 0);
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting=intent.getStringExtra("categories");


        final Animation animAlpha = AnimationUtils.loadAnimation(this,
                R.anim.alpha);

        catMusica=(ImageButton)findViewById(R.id.imgMusica);
        catActivismo=(ImageButton)findViewById(R.id.imgActivismo);
        catDeportes=(ImageButton)findViewById(R.id.imgDeporte);
        catSalidas=(ImageButton)findViewById(R.id.imgSalidas);
        catFlash=(ImageButton)findViewById(R.id.imgFlash);
        catMascotas=(ImageButton)findViewById(R.id.imgMascotas);

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
                                Animation shake = AnimationUtils.loadAnimation(NuevoHatch.this, R.anim.shake);

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
                if ( titulo.getText().toString().equals("") || titulo.getText().toString().equals("Insert Title Here!")){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatch.this);
                    dialogBuilder.setMessage("Please insert a Title for the Hatch before proceeding");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }
                else {
                    view.startAnimation(animAlpha);
                    categorias = "Musica";
                    Intent mapa = new Intent(getBaseContext(), actividadDrawer.class);
                    mapa.putExtra("Titulo", titulo.getText().toString());
                    mapa.putExtra("Categoria", categorias.toString());
                    mapa.putExtra("username", username);
                    mapa.putExtra("User_Id", User_Id);
                    mapa.putExtra("categories", userCategoriesSetting);
                    mapa.putExtra("distanceSetting", userDistanceSetting);

                    startActivity(mapa);
                    finish();
                }
            }
        });

        catActivismo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ( titulo.getText().toString().equals("") || titulo.getText().toString().equals("Insert Title Here!")){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatch.this);
                    dialogBuilder.setMessage("Please insert a Title for the Hatch before proceeding");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }else {
                    view.startAnimation(animAlpha);
                    categorias = "Activismo";
                    Intent mapa = new Intent(getBaseContext(), actividadDrawer.class);
                    mapa.putExtra("Titulo", titulo.getText().toString());
                    mapa.putExtra("Categoria", categorias.toString());
                    mapa.putExtra("username", username);
                    mapa.putExtra("User_Id", User_Id);
                    mapa.putExtra("categories", userCategoriesSetting);
                    mapa.putExtra("distanceSetting", userDistanceSetting);
                    startActivity(mapa);
                    finish();
                }
            }
        });

        catDeportes.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ( titulo.getText().toString().equals("") || titulo.getText().toString().equals("Insert Title Here!")){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatch.this);
                    dialogBuilder.setMessage("Please insert a Title for the Hatch before proceeding");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }else{
                    view.startAnimation(animAlpha);
                    categorias="Deportes";
                    Intent mapa=new Intent(getBaseContext(),actividadDrawer.class);
                    mapa.putExtra("Titulo", titulo.getText().toString());
                    mapa.putExtra("Categoria", categorias.toString());
                    mapa.putExtra("username",username);
                    mapa.putExtra("User_Id",User_Id);
                    mapa.putExtra("categories",userCategoriesSetting);
                    mapa.putExtra("distanceSetting",userDistanceSetting);
                    startActivity(mapa);
                    finish();}

            }
        });

        catSalidas.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ( titulo.getText().toString().equals("") || titulo.getText().toString().equals("Insert Title Here!")){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatch.this);
                    dialogBuilder.setMessage("Please insert a Title for the Hatch before proceeding");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }else {
                    view.startAnimation(animAlpha);
                    categorias = "Salidas";
                    Intent mapa = new Intent(getBaseContext(), actividadDrawer.class);
                    mapa.putExtra("Titulo", titulo.getText().toString());
                    mapa.putExtra("Categoria", categorias.toString());
                    mapa.putExtra("username", username);
                    mapa.putExtra("User_Id", User_Id);
                    mapa.putExtra("categories", userCategoriesSetting);
                    mapa.putExtra("distanceSetting", userDistanceSetting);
                    startActivity(mapa);
                    finish();
                }
            }
        });

        catFlash.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ( titulo.getText().toString().equals("") || titulo.getText().toString().equals("Insert Title Here!")){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatch.this);
                    dialogBuilder.setMessage("Please insert a Title for the Hatch before proceeding");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }else {
                    view.startAnimation(animAlpha);
                    categorias = "Flash";
                    Intent mapa = new Intent(getBaseContext(), actividadDrawer.class);
                    mapa.putExtra("Titulo", titulo.getText().toString());
                    mapa.putExtra("Categoria", categorias.toString());
                    mapa.putExtra("username", username);
                    mapa.putExtra("User_Id", User_Id);
                    mapa.putExtra("categories", userCategoriesSetting);
                    mapa.putExtra("distanceSetting", userDistanceSetting);
                    startActivity(mapa);
                    finish();
                }
            }
        });

        catMascotas.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ( titulo.getText().toString().equals("") || titulo.getText().toString().equals("Insert Title Here!")){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NuevoHatch.this);
                    dialogBuilder.setMessage("Please insert a Title for the Hatch before proceeding");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }else {
                    view.startAnimation(animAlpha);
                    categorias = "Mascotas";
                    Intent mapa = new Intent(getBaseContext(), actividadDrawer.class);
                    mapa.putExtra("Titulo", titulo.getText().toString());
                    mapa.putExtra("Categoria", categorias.toString());
                    mapa.putExtra("username", username);
                    mapa.putExtra("User_Id", User_Id);
                    mapa.putExtra("categories", userCategoriesSetting);
                    mapa.putExtra("distanceSetting", userDistanceSetting);
                    startActivity(mapa);
                    finish();
                }
            }
        });

        //endregion
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_nuevo_hatch);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
