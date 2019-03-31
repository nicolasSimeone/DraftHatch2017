package nupa.drafthatch;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;


public class MainActivityDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener/*,AsyncResponseCounter,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener */{

    //region Variables
    private String[] stringArray;  //Opciones del menu
    private ArrayAdapter itemArrayAdapter;
    public ArrayList<Hatch> Hatchs = new ArrayList<Hatch>();  //Lista de Hatchs del newsfeed
    private TextView Cargando,usernameMenu;  //Text "Cargando"mientras carga el newsfeed
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private ListView listaHatchs;
    private LatLng latLng;
    private Geocoder geocoder;
    private List<Address> addresses=null;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double distancia;
    private SwipeRefreshLayout refreshLayout;
    //private AsyncCall task, taskRefresh;
    private String username;
    private int User_Id;

    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
    static String userDistanceSetting;
    static String userCategoriesSetting;
    double distanciaM, distanciaKm;
    UserLocalStore userLocalStore;
    List<String> CategoriesList ;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_drawer);

        //region Dibujado menu

       // getSupportActionBar().setTitle("Hatches!");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("HatchFeed");

        FloatingActionButton fabNewHatch = (FloatingActionButton) findViewById(R.id.fabNewHatch);
        fabNewHatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newHatch=new Intent(getBaseContext(), NuevoHatchStep1.class);  //Abro la actividad del nuevo hatch
                newHatch.putExtra("username",username);
                newHatch.putExtra("User_Id",User_Id);
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
        navigationView.setNavigationItemSelectedListener(this);

        //endregion

      /*  Intent intent=getIntent();
        username= intent.getStringExtra("username");
        User_Id=intent.getIntExtra("User_Id", 0);
        task=new AsyncCall();  //Llamo a un hilo diferente para ir a buscar a la BD
        Cargando=(TextView)findViewById(R.id.txtCargando);
        itemArrayAdapter=new Adapter(this,Hatchs);
        parametros.add(new BasicNameValuePair("Username",username));


        //region Conexion Api google services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        //endregion

        //region Validacion GPS
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Los servicios de localización no están encendidos");
            builder.setMessage("Por favor active el GPS");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }else{
            mGoogleApiClient.connect();
            taskDistanceSetting=new AsyncCallDistanceSetting();
            taskDistanceSetting.interfazCounter=this;
            taskDistanceSetting.execute(String.valueOf(username).toString());

        }
        //endregion



        //region RefreshLayout
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                taskRefresh=new AsyncCall();
                Hatchs.clear();
                taskDistanceSettingOnRefresh=new AsyncCallDistanceSetting();
                taskDistanceSettingOnRefresh.interfazCounter=MainActivityDrawer.this;
                taskDistanceSettingOnRefresh.execute(String.valueOf(username).toString());
                taskRefresh.execute();
            }
        });
        //endregion



    }*/

   /*
    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if(mLastLocation!=null){
            latLng=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        }

        task.execute();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void processFinish(List<String> output) {
        for (int i=0;i < output.size();i++)
        {
            userDistanceSetting= output.get(i);
        }
        Toast.makeText(getApplicationContext(), "DISTANCE: " + userDistanceSetting,Toast.LENGTH_LONG).show();
        setUserDistanceSetting(userDistanceSetting);
    }

    @Override
    public void processFinishCategories(List<String> output) {
        for (int i=0;i < output.size();i++)
        {
            userCategoriesSetting= output.get(i);

        }

        setUserCategoriesSetting(userCategoriesSetting);
        CategoriesList = Arrays.asList(userCategoriesSetting.split(","));
    }

    public class AsyncCall extends AsyncTask<String, Void, List<Hatch>> {

        @Override
        protected List<Hatch> doInBackground(String... params) {  //doInBackground se ejecuta de fondo
            String result="";
            InputStream is=null;

            //region Conexion a la DB
            try{
                HttpClient httpclient =new DefaultHttpClient();
                HttpPost httppost=new HttpPost("http://nupa.com.ar/test.php");  //Conecto a mi script de la BD
                httppost.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response=httpclient.execute(httppost);
                HttpEntity entity=response.getEntity();
                is=entity.getContent();
            }catch (Exception e){
                Log.e("log_tag","Error in Http connection "+e.toString());
            }
            //endregion

            //region Lectura datos de la DB
            try{
                BufferedReader reader=new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                StringBuilder sb=new StringBuilder();
                String line=null;
                while ((line=reader.readLine())!=null){
                    sb.append(line + "\n");
                }
                is.close();
                result=sb.toString();
            }catch (Exception e){
                Log.e("Log_tag","Error converting result "+e.toString());
            }
            //endregion


            //region Set localilacion del User
            Location locacionUser=new Location("PuntoA");
            locacionUser.setLatitude(latLng.latitude);
            locacionUser.setLongitude(latLng.longitude);
            //endregion

            //region Parseo Json y seteo lista de Hatchs
            //parse Json, seteo el objeto hatch con los valores de la BD y los pongo en una lista
            try{
                JSONArray jArray=new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data=jArray.getJSONObject(i);
                    Hatch Hatch=new Hatch();
                    Hatch.setTitle(json_data.getString("hatch_title"));
                    Hatch.setBody(json_data.getString("hatch_description"));
                    Hatch.setUbicacion_lon(json_data.getString("hatch_location_lon"));
                    Hatch.setUbicacion_lat(json_data.getString("hatch_location_lat"));
                    Hatch.setCategoria(json_data.getString("hatch_category"));
                    Location locacionHatch=new Location("PuntoB");
                    locacionHatch.setLatitude(json_data.getDouble("hatch_location_lat"));
                    locacionHatch.setLongitude(json_data.getDouble("hatch_location_lon"));
                    distancia=locacionUser.distanceTo(locacionHatch);
                    Hatch.setUser_Id(json_data.getInt("id_user"));
                    Hatch.setUsername(json_data.getString("username"));
                    Hatch.setDireccion(json_data.getString("hatch_direccion"));
                    Hatch.setTipo(json_data.getString("hatch_type"));
                    Hatch.setId_Hatch(json_data.getInt("id_hatch"));
                    Hatch.setFecha(json_data.getString("hatch_end_date"));
                    Hatch.setFechaInicio(json_data.getString("hatch_start_date"));
                    Hatch.setFechaFin(json_data.getString("hatch_end_date"));
                    Hatch.setDistancia(distancia);

                   distanciaM=distancia;
                    distanciaKm=distanciaM*0.001;
                    if (distanciaKm <= Double.parseDouble(userDistanceSetting)){
                        if (checkCategoriesList(Hatch.getCategoria(),CategoriesList) == 1) {
                            Hatchs.add(Hatch);
                        }
                    }
                }
            }catch (JSONException e){
                Log.e("Log_tag", "Error parsing data "+e.toString());
            }
            //endregion

            return Hatchs;
        }


        private Void onPreExecute(Void result){
            super.onPreExecute();
            return null;
        }

        @Override
        protected void onPostExecute(List<Hatch> hatches) {  //Se ejecuta luego de que doInBackground termino de procesar
            super.onPostExecute(hatches);

            listaHatchs=(ListView)findViewById(R.id.ListaHatch);
            listaHatchs.setAdapter(itemArrayAdapter);
            Cargando.setText("");

            AdapterView.OnItemClickListener listener =new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //Click en el detalle del hatch

                    Intent intent=new Intent(getApplicationContext(),ActividadDetalleHatch.class);
                    Bundle b=new Bundle();
                    String TextTitulo=Hatchs.get(position).getTitle();
                    String TextBody=Hatchs.get(position).getBody();
                    Double Distancia=Hatchs.get(position).getDistancia();
                    String UserNameHatch=Hatchs.get(position).getUsername();
                    String Direccion=Hatchs.get(position).getDireccion();
                    String Tipo=Hatchs.get(position).getTipo();
                    int Id_Hatch=Hatchs.get(position).getId_Hatch();
                    String Fecha=Hatchs.get(position).getFecha();
                    String FechaInicio=Hatchs.get(position).getFechaInicio();
                    String FechaFin=Hatchs.get(position).getFechaFin();

                    intent.putExtra("Titulo", TextTitulo);
                    intent.putExtra("Cuerpo",TextBody);
                    intent.putExtra("Distancia",Distancia);
                    intent.putExtra("Usuario", UserNameHatch);
                    intent.putExtra("Direccion",Direccion);
                    intent.putExtra("Tipo",Tipo);
                    intent.putExtra("Id_Hatch",Id_Hatch);
                    intent.putExtra("UsuarioLogueado",username);
                    intent.putExtra("Fecha",Fecha);
                    intent.putExtra("FechaInicio", FechaInicio);
                    intent.putExtra("FechaFin",FechaFin);
                    startActivity(intent);

                }
            };

            listaHatchs.setOnItemClickListener(listener); // adjunto listener a la lista de Hatchs cuando se hace click

            refreshLayout.setRefreshing(false);

        }*/
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
            settings.putExtra("username",username);
            settings.putExtra("distanceSetting",userDistanceSetting);
            startActivity(settings);
            // Handle the camera action
        } else if (id == R.id.nav_Hatches) {
            Intent Hatches=new Intent(getBaseContext(),HatchesNotificationActivity.class);
            Hatches.putExtra("username",username);
            startActivity(Hatches);

      //  } else if (id == R.id.nav_inbox) {


        } else if (id == R.id.nav_notifications) {
            Intent intentAprobar=new Intent(getBaseContext(),ActividadNotificaciones.class);
            intentAprobar.putExtra("username",username);
            startActivity(intentAprobar);

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_logout) {
            // Session Manager Class

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    public void setUserDistanceSetting(String distance){
        userDistanceSetting = distance;
    }

    public void setUserCategoriesSetting(String categories){
        userCategoriesSetting = categories;

    }

    public int checkCategoriesList(String str, List<String> categories)
    {
        int temp = 2;
        if(categories.contains(str)) temp=1;
        return temp;
    }

    @Override
    public void processFinishHatchs(List<Hatch>output) {

    }*/
}
