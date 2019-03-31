package nupa.drafthatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ActividadNotificaciones extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String username;
    private ArrayAdapter itemArrayAdapter;
    public List<Hatch> Hatchs = new ArrayList<Hatch>();
    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> parametrosApproveDeny = new ArrayList<NameValuePair>();
    private SwipeMenuListView listaHatchs;
    public SwipeActionAdapter mAdapter;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    private int User_id;

    private Bitmap toyImageScaled;
    static String name;
    static String email;
    static Uri photoUrl;
    static String uid;

    //FIREBASE
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private int CAMERA_REQUEST_CODE = 0;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    public ImageView imageView;
    RoundImage roundedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones_approval_drawer);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl= user.getPhotoUrl();
            uid = user.getUid();

        }
        else{
            Intent intentt = new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(intentt);
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarNotificationApproval);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notificaciones");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_notificaciones_approval);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = LayoutInflater.from(ActividadNotificaciones.this).inflate(R.layout.nav_header_main_activity_drawer, null);
        navigationView.addHeaderView(header);


        //ADDING HEADER
        imageView = (ImageView)header.findViewById(R.id.imageView4);

        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {
            mStorage = FirebaseStorage.getInstance().getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            //  mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String imageUrl = dataSnapshot.child("image").getValue().toString();
                    if (!imageUrl.equals("default") || TextUtils.isEmpty(imageUrl)) {
                        // Picasso.with(getBaseContext()).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).into(imageView);
                        Picasso.with(getBaseContext()).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).resize(200, 200).transform(new CircleTransform()).into(imageView);

                    } else {

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //SET USERPROFILE EMAIL IN HEADER
        TextView texto = (TextView) header.findViewById(R.id.txtUsername);
        texto.setText(email);
        texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentProfile = new Intent(getBaseContext(), ProfileActivity.class);
                intentProfile.putExtra("username", username);
                intentProfile.putExtra("distanceSetting", userDistanceSetting);
                intentProfile.putExtra("categories", userCategoriesSetting);
                intentProfile.putExtra("User_Id", User_id);
                startActivity(intentProfile);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        Intent intent=getIntent();
        username= intent.getStringExtra("username");
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting=intent.getStringExtra("categories");
        User_id=intent.getIntExtra("User_Id", 0);

        parametros.add(new BasicNameValuePair("Username",username));
        itemArrayAdapter=new AdapterAprobacion(this,Hatchs);


        String registrationID = FirebaseInstanceId.getInstance().getToken();


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_notificaciones_approval);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent settings=new Intent(getBaseContext(), user_settings.class);  //Abro la actividad del nuevo hatch
            settings.putExtra("username",username);
            settings.putExtra("categories",userCategoriesSetting);
            settings.putExtra("distanceSetting",userDistanceSetting);
            settings.putExtra("User_Id", User_id);
            startActivity(settings);
            // Handle the camera action
        } else if (id == R.id.nav_Hatches) {
            Intent Hatches=new Intent(getBaseContext(),HatchesNotificationActivity.class);
            Hatches.putExtra("username",username);
            Hatches.putExtra("categories",userCategoriesSetting);
            Hatches.putExtra("distanceSetting",userDistanceSetting);
            Hatches.putExtra("User_Id", User_id);
            startActivity(Hatches);

       // } else if (id == R.id.nav_inbox) {


        } else if (id == R.id.nav_notifications) {
            Intent intentAprobar=new Intent(getBaseContext(),ActividadNotificaciones.class);
            intentAprobar.putExtra("username",username);
            intentAprobar.putExtra("categories", userCategoriesSetting);
            intentAprobar.putExtra("distanceSetting",userDistanceSetting);
            intentAprobar.putExtra("User_Id", User_id);
            startActivity(intentAprobar);

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_notificaciones_approval);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


