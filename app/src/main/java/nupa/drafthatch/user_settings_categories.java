package nupa.drafthatch;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class user_settings_categories extends AppCompatActivity  {



    private String [] categories = new String[5];
    static String distanceSetting;
    static String categoriestoDB;
    String categoriesList[] = new String [5];
  ImageView imgFlash,imgMascotas ,imgMusica ,imgDeporte ,imgSalidas ,imgActivismo ;
    //NEW LOGIN FIREBASE
    private static final String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button butConfirmar;
    //END NEW LOGIN


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_categoria);

        Intent intent=getIntent();


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

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("categories");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoriestoDB = dataSnapshot.getValue(String.class);
                List<String> categoriesList = Arrays.asList(categoriestoDB.split(","));
                cargarCategorias(categoriesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //categoriestoDB = "Deportes,Musica,Activismo,Mascotas,Salidas,Flash";




        butConfirmar = (Button)findViewById(R.id.butListo);
        imgFlash = (ImageView)findViewById(R.id.imgFlash);
        imgMascotas = (ImageView)findViewById(R.id.imgMascotas);
        imgMusica = (ImageView)findViewById(R.id.imgMusica);
        imgDeporte = (ImageView)findViewById(R.id.imgDeportes);
        imgSalidas = (ImageView)findViewById(R.id.imgSalidas);
        imgActivismo = (ImageView)findViewById(R.id.imgActivismo);

       // img.setTag(R.id.img1);
       // if(((int)img.getTag() ) == R.id.img1) {


//PRECARGA

//

 //ONCLICK LISTENER PARA CHECK Y UNCHECK
        imgMascotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)imgMascotas.getTag() == R.drawable.mascotas_icon_big_selected) {
                    imgMascotas.setImageResource(R.drawable.mascotas_icon_big);
                    categories[3] = "0";
                    imgMascotas.setTag(R.drawable.mascotas_icon_big);
                }
                    else{
                        imgMascotas.setImageResource(R.drawable.mascotas_icon_big_selected);
                    categories[3] = "Mascotas";
                    imgMascotas.setTag(R.drawable.mascotas_icon_big_selected);
                    }

            }
        });
        imgMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)imgMusica.getTag() == R.drawable.musica_icon_big_selected) {
                    imgMusica.setImageResource(R.drawable.musica_icon_big);
                    categories[1] = "0";
                    imgMusica.setTag(R.drawable.musica_icon_big);
                }
                else{
                    imgMusica.setImageResource(R.drawable.musica_icon_big_selected);
                    categories[1] = "Musica";
                    imgMusica.setTag(R.drawable.musica_icon_big_selected);
                }
            }
        });
        imgDeporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)imgDeporte.getTag() == R.drawable.deportes_icon_big_selected) {
                    imgDeporte.setImageResource(R.drawable.deportes_icon_big);
                    categories[0] = "0";
                    imgDeporte.setTag(R.drawable.deportes_icon_big);
                }
                else{
                    imgDeporte.setImageResource(R.drawable.deportes_icon_big_selected);
                    categories[0] = "Deportes";
                    imgDeporte.setTag(R.drawable.deportes_icon_big_selected);
                }
            }
        });
        imgSalidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)imgSalidas.getTag() == R.drawable.salidas_icon_big_selected) {
                    imgSalidas.setImageResource(R.drawable.salidas_icon_big);
                    categories[4] = "0";
                    imgSalidas.setTag(R.drawable.salidas_icon_big);
                }
                else{
                    imgSalidas.setImageResource(R.drawable.salidas_icon_big_selected);
                    categories[4] = "Salidas";
                    imgSalidas.setTag(R.drawable.salidas_icon_big_selected);
                }
            }
        });
        imgActivismo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)imgActivismo.getTag() == R.drawable.activismo_icon_big_selected) {
                    imgActivismo.setImageResource(R.drawable.activismo_icon_big);
                    categories[2] = "0";
                    imgActivismo.setTag(R.drawable.activismo_icon_big);
                }
                else{
                    imgActivismo.setImageResource(R.drawable.activismo_icon_big_selected);
                    categories[2] = "Activismo";
                    imgActivismo.setTag(R.drawable.activismo_icon_big_selected);
                }
            }
        });

        butConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoriestoDB = categories[0]+","+categories[1]+","+categories[2]+","+categories[3]+","+categories[4]+",Flash";

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                currentUserDB.child("categories").setValue(categoriestoDB);
                currentUserDB.child("firstLogin").setValue("1");

                Intent intent2 = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent2);
                finish();

            }
        });
//

    }




    public void cargarCategorias(List<String> categoriesList){
        imgFlash.setImageResource(R.drawable.flash_icon_big_selected);

        if (categoriesList.get(0).equals("0")) {
            imgDeporte.setImageResource(R.drawable.deportes_icon_big);
            categories[0] = "0";
            imgDeporte.setTag(R.drawable.deportes_icon_big);
        }else {
            imgDeporte.setImageResource(R.drawable.deportes_icon_big_selected);
            categories[0] = "Deportes";
            imgDeporte.setTag(R.drawable.deportes_icon_big_selected);        }
        if (categoriesList.get(1).equals("0")) {
            imgMusica.setImageResource(R.drawable.musica_icon_big);
            categories[1] = "0";
            imgMusica.setTag(R.drawable.musica_icon_big);
        }else {
            imgMusica.setImageResource(R.drawable.musica_icon_big_selected);
            categories[1] = "Musica";
            imgMusica.setTag(R.drawable.musica_icon_big_selected);
        }
        if (categoriesList.get(2).equals("0")) {
            imgActivismo.setImageResource(R.drawable.activismo_icon_big);
            categories[2] = "0";
            imgActivismo.setTag(R.drawable.activismo_icon_big);
        }else {
            imgActivismo.setImageResource(R.drawable.activismo_icon_big_selected);
            categories[2] = "Activismo";
            imgActivismo.setTag(R.drawable.activismo_icon_big_selected);
        }
        if (categoriesList.get(3).equals("0")) {
            imgMascotas.setImageResource(R.drawable.mascotas_icon_big);
            categories[3] = "0";
            imgMascotas.setTag(R.drawable.mascotas_icon_big);
        }else {
            imgMascotas.setImageResource(R.drawable.mascotas_icon_big_selected);
            categories[3] = "Mascotas";
            imgMascotas.setTag(R.drawable.mascotas_icon_big_selected);
        }
        if (categoriesList.get(4).equals("0")) {
            imgSalidas.setImageResource(R.drawable.salidas_icon_big);
            categories[4] = "0";
            imgSalidas.setTag(R.drawable.salidas_icon_big);
        }else {
            imgSalidas.setImageResource(R.drawable.salidas_icon_big_selected);
            categories[4] = "Salidas";
            imgSalidas.setTag(R.drawable.salidas_icon_big_selected);
        }

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
    public void onBackPressed() {


        super.onBackPressed();




      ;
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


