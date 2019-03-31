package nupa.drafthatch;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class user_settings extends AppCompatActivity  {


TextView txtLocacion, txtCategorias,txtLegal,txtPoliticasPrivacidad,txtTerminosyCondiciones;

    static String distanceSetting;
    static String categoriestoDB;
    SeekBar distanceBar;
    TextView distanceInfo;
    CheckBox checkNotificacion;

    //NEW LOGIN FIREBASE
    private static final String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //END NEW LOGIN


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);



        txtLocacion=(TextView)findViewById(R.id.txtLocacion) ;
        txtCategorias=(TextView)findViewById(R.id.txtCategorias) ;
        txtLegal=(TextView)findViewById(R.id.txtLegal) ;
        txtPoliticasPrivacidad=(TextView)findViewById(R.id.txtPoliticasPrivacidad) ;
        txtTerminosyCondiciones=(TextView)findViewById(R.id.txtTerminosyCondiciones) ;
        checkNotificacion = (CheckBox)findViewById(R.id.checkNotificaciones);

        txtCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());

                int distance;
                distance = (distanceBar.getProgress() );
                String distanceDirebase = String.valueOf(distance);
                currentUserDB.child("distance").setValue(distanceDirebase);
                currentUserDB.child("firstLogin").setValue("1");

                if(checkNotificacion.isChecked()){
                    currentUserDB.child("flag_notification").setValue("1");
                }else{
                    currentUserDB.child("flag_notification").setValue("0");
                }
                Intent intent2 = new Intent(getApplicationContext(), user_settings_categories.class);
                startActivity(intent2);
                finish();
            }
        });

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


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                distanceSetting = dataSnapshot.child("distance").getValue().toString();
                String notificacion = dataSnapshot.child("flag_notification").getValue().toString();
                if (notificacion.equals("0")){
                    checkNotificacion.setChecked(false);
                }else {
                    checkNotificacion.setChecked(true);
                }


                //PRECARGA
                if (distanceSetting.equals("1")){
                    distanceBar.setProgress(1);
                }else{
                    setDistanceValue(Integer.parseInt(distanceSetting));
                }
                distanceInfo.setText(distanceSetting);
                //
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //distanceSetting = "50";
        categoriestoDB = "Deportes,Musica,Activismo,Mascotas,Salidas,Flash";

        distanceBar = (SeekBar)findViewById(R.id.bar_distance);
        distanceInfo = (TextView)findViewById(R.id.distance_info);

        OnSeekBarChangeListener distanceBarChangeListener
                = new OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2)
            {
                if (arg0.getProgress()==0){
                    arg0.setProgress(1);
                }
                setDistanceValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        };
        distanceBar.setOnSeekBarChangeListener(distanceBarChangeListener);


    }


    private void setDistanceValue() {

        int distance = (int) (distanceBar.getProgress() );
        distanceInfo.setText(String.valueOf(distance));
    }

    private void setDistanceValue(int d){

        int distance = d;
        distanceBar.setProgress(d);
        distanceInfo.setText(String.valueOf(distance));
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

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());

        int distance;
        distance = (distanceBar.getProgress() );
        //FORMAT DISTANCE BEFORE WRITE DATABASE
        String distanceDirebase = String.valueOf(distance);
        currentUserDB.child("distance").setValue(distanceDirebase);
       // currentUserDB.child("categories").setValue(categoriestoDB);
        currentUserDB.child("firstLogin").setValue("1");
        if(checkNotificacion.isChecked()){
            currentUserDB.child("flag_notification").setValue("1");
        }else{
            currentUserDB.child("flag_notification").setValue("0");
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


