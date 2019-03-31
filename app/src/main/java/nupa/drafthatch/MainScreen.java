package nupa.drafthatch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    private String username;
    private int User_id;
    private boolean doubleBackToExitPressedOnce = false;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public Location locacionUserPuntoA=new Location("PuntoA");
    private LatLng latLng;
    private  ProgressDialog progress;

    private Bitmap toyImageScaled;
    TextView txtUsername;

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
    Status status2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_drawer);

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

        final Intent intent=getIntent();

        username = intent.getStringExtra("username");
        User_id=intent.getIntExtra("User_Id", 0);
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting=intent.getStringExtra("categories");

        progress = new ProgressDialog(MainScreen.this);
        progress.setTitle("Cargando");
        progress.setMessage("Espere por favor");
        progress.show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Eventos!");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewHatch);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newHatch = new Intent(getBaseContext(), NuevoHatchStep1.class);  //Abro la actividad del nuevo hatch
                newHatch.putExtra("username", username);
                newHatch.putExtra("User_Id", User_id);
                newHatch.putExtra("distanceSetting", userDistanceSetting);
                newHatch.putExtra("categories", userCategoriesSetting);

                finish();
                startActivity(newHatch);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = LayoutInflater.from(MainScreen.this).inflate(R.layout.nav_header_main_activity_drawer, null);
        navigationView.addHeaderView(header);


        //ADDING HEADER
        imageView = (ImageView)header.findViewById(R.id.imageView4);

        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {
            mStorage = FirebaseStorage.getInstance().getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
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
        final  TextView texto = (TextView) header.findViewById(R.id.txtUsername);

        if (name==null || name.equals("")){
           // DatabaseReference getUserName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("name");
            DatabaseReference getUserName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

            getUserName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String user_name  =dataSnapshot.child("name").getValue().toString();
                    String user_lastName  =dataSnapshot.child("apellido").getValue().toString();
                    texto.setText(user_name +" " + user_lastName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            texto.setText(name);
        }

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

        viewPager = (ViewPager) findViewById(R.id.viewpagerMain);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();


            //Una vez que conecto el cliente de google armo el location request para mostrar el encendido del GPS
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10 * 1000);
            locationRequest.setFastestInterval(1 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);


            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() { //obtengo el resultado de los settings actuales
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    status2 = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            //El GPS ya estaba prendido por lo que calculo la locación del usuario y seteo los fragments
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                    mGoogleApiClient);

                            while (mLastLocation==null){
                                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                        mGoogleApiClient);
                            }

                            if(mLastLocation!=null){
                                latLng=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                            }

                            locacionUserPuntoA.setLatitude(latLng.latitude);
                            locacionUserPuntoA.setLongitude(latLng.longitude);

                            setupViewPager(viewPager);
                            tabLayout = (TabLayout) findViewById(R.id.tabsMain);
                            tabLayout.setupWithViewPager(viewPager);
                            progress.dismiss();

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                          //El GPS no estaba prendido
                            try {
                                //Muestro el dialog del GPS y capturo el resultado en el activityResult
                                status.startResolutionForResult(
                                        MainScreen.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }


            });
        }
    }

    //Capturo la respuesta del encendido del GPS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                      //Prendio el GPS por lo que calculo distancia y genero fragments

                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);

                        //La obtención de la locación no es instantanea por lo que hago un loop hasta obtenerla para luego poder usarlo después
                        while (mLastLocation==null){
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                    mGoogleApiClient);
                        }
                        if(mLastLocation!=null){

                            latLng=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                        }

                        locacionUserPuntoA.setLatitude(latLng.latitude);
                        locacionUserPuntoA.setLongitude(latLng.longitude);

                        setupViewPager(viewPager);
                        tabLayout = (TabLayout) findViewById(R.id.tabsMain);
                        tabLayout.setupWithViewPager(viewPager);
                        progress.dismiss();

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        try {
                            //Muestro el dialog del GPS y capturo el resultado en el activityResult
                            status2.startResolutionForResult(
                                    MainScreen.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }

                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HatchFeedFragment(), "Lista");
        adapter.addFragment(new MapFragment(), "Mapa");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                minimizeApp();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Presione de nuevo para salir", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_drawer, menu);

               return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent settings=new Intent(getBaseContext(), user_settings.class);  //Abro la actividad del nuevo hatch
            settings.putExtra("username",username);
            settings.putExtra("distanceSetting",userDistanceSetting);
            settings.putExtra("categories",userCategoriesSetting);
            startActivity(settings);
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
            intentAprobar.putExtra("categories",userCategoriesSetting);
            intentAprobar.putExtra("distanceSetting",userDistanceSetting);
            startActivity(intentAprobar);

        } else if (id == R.id.nav_support) {
            Intent support = new Intent(getBaseContext(),AyudaSoporteActivity.class);
            startActivity(support);

        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

            startActivity((new Intent(getBaseContext(), Login_Activity.class))); //Logout
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
