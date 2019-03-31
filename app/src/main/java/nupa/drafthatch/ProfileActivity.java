package nupa.drafthatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends  AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String username;
    static String userDistanceSetting;
    static String userCategoriesSetting;
     private int User_id;

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
        setContentView(R.layout.activity_profile_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();

        username= intent.getStringExtra("username");
        User_id=intent.getIntExtra("User_Id", 0);
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting=intent.getStringExtra("categories");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        //ADDING HEADER
        imageView = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView4);

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
                        Picasso.with(getBaseContext()).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).resize(150, 150).transform(new CircleTransform()).into(imageView);

                    } else {

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //SET USERPROFILE EMAIL IN HEADER
        final  TextView texto = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtUsername);

        if (name==null || name.equals("")){
           // DatabaseReference getUserName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("name");
            DatabaseReference getUserName = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

            getUserName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //String user_name  =dataSnapshot.getValue(String.class);
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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            settings.putExtra("username",username);//A
            settings.putExtra("categories",userCategoriesSetting);
            settings.putExtra("distanceSetting",userDistanceSetting);
            // settings.putExtra("distanceSetting",userDistanceSetting);
            startActivity(settings);
            // Handle the camera action
        } else if (id == R.id.nav_Hatches) {
            Intent Hatches=new Intent(getBaseContext(),MainScreen.class);
            Hatches.putExtra("username",username);//A
            Hatches.putExtra("categories",userCategoriesSetting);
            Hatches.putExtra("distanceSetting",userDistanceSetting);
            startActivity(Hatches);

      //  } else if (id == R.id.nav_inbox) {


        } else if (id == R.id.nav_notifications) {
            Intent intentAprobar=new Intent(getBaseContext(),ActividadNotificaciones.class);
            intentAprobar.putExtra("username",username);//A
            intentAprobar.putExtra("categories",userCategoriesSetting);
            intentAprobar.putExtra("distanceSetting",userDistanceSetting);
            startActivity(intentAprobar);

        } else if (id == R.id.nav_support) {
            Intent ayudaSoporte = new Intent(getBaseContext(), AyudaSoporteActivity.class);
            startActivity(ayudaSoporte);

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileInfoFragment(), "Info");
       // adapter.addFragment(new ReputationFragment(), "Reputación");
        //adapter.addFragment(new KarmaFragment(),"Karma");
        viewPager.setAdapter(adapter);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
}
