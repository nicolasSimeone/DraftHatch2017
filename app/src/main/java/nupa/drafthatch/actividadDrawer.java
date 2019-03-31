package nupa.drafthatch;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class actividadDrawer extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private GoogleMap map;
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendarEnd = Calendar.getInstance();
    Calendar fechahoy = Calendar.getInstance();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;
    private Button enviarHatch;
    private ImageView buscarDireccion;
    private Geocoder geocoder;
    private List<Address> addresses=null;
    private AutoCompleteTextView autoCompleteUbicacion;
    private EditText fecha, descripcion;
    private Spinner participantes;
    private String username, direccion, direccionFinal, tipo="Abierto";
    private int User_Id;
    private Switch switchTipo;
    ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
    private String[] numneroParticipantes= new String[31];
    private Date FechaInicio, FechaFin;
    String strFechaInicio,strFechaFin;
    static String userDistanceSetting;
    static String userCategoriesSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_drawer);

        for (int i=0;i<31;i++){
            numneroParticipantes[i]=String.valueOf(i+1);
        }

        final Intent intent=getIntent();

        username= intent.getStringExtra("username");
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting=intent.getStringExtra("categories");

        User_Id=intent.getIntExtra("User_Id",0);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        fecha=(EditText)findViewById(R.id.txtFecha);
        buscarDireccion=(ImageView)findViewById(R.id.imgBuscarDireccion);
        geocoder=new Geocoder(this,Locale.getDefault());
        enviarHatch=(Button)findViewById(R.id.btnEnviarHatch);
        descripcion=(EditText)findViewById(R.id.txtDetalle);
        participantes=(Spinner)findViewById(R.id.SpinnerParticipantes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, numneroParticipantes);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        descripcion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    descripcion.setText("");

                }

                return false;
            }
        });

        participantes.setAdapter(new NothingSelectedSpinnerAdapter(adapter,R.layout.spinner_participantes,getBaseContext()));

        participantes.setPrompt("Participantes");



        enviarHatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parametros.add(new BasicNameValuePair("Categoria",intent.getStringExtra("Categoria")));
                parametros.add(new BasicNameValuePair("Titulo",intent.getStringExtra("Titulo")));
                parametros.add(new BasicNameValuePair("Latitud",Double.toString(latLng.latitude)));
                parametros.add(new BasicNameValuePair("Longitud",Double.toString(latLng.longitude)));
                parametros.add(new BasicNameValuePair("Cuerpo",descripcion.getText().toString()));
                parametros.add(new BasicNameValuePair("User_Id",String.valueOf(User_Id)));
                parametros.add(new BasicNameValuePair("Username",username));
                parametros.add(new BasicNameValuePair("Direccion",autoCompleteUbicacion.getText().toString()));
                parametros.add(new BasicNameValuePair("Tipo",tipo));
                parametros.add(new BasicNameValuePair("FechaInicio", strFechaInicio));
                parametros.add(new BasicNameValuePair("FechaFin", strFechaFin));
                if(tipo=="Abierto"){
                    parametros.add(new BasicNameValuePair("Participantes","0"));
                }else{
                    parametros.add(new BasicNameValuePair("Participantes",participantes.getSelectedItem().toString()));
                }
                boolean CANCREATEHATCH = true;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date2 = dateFormat.parse(strFechaFin);
                    Date date1 = dateFormat.parse(strFechaInicio);

                    long different = date2.getTime()-date1.getTime();
                    long dateEnd = date2.getTime();
                    long dateStart = date1.getTime();
                    long elapsedHours= TimeUnit.MILLISECONDS.toHours(different);
                    long elapsedEnd= TimeUnit.MILLISECONDS.toHours(dateEnd);
                    long elapsedStart= TimeUnit.MILLISECONDS.toHours(dateStart);
                    if ((elapsedEnd < elapsedStart) || (intent.getStringExtra("Categoria").equals("Flash") && (elapsedHours >2))){
                        if((elapsedEnd < elapsedStart)) {
                            AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(actividadDrawer.this);
                            dialogBuilder2.setMessage("End Date should be mayor than Start Date! Please modify...");
                            dialogBuilder2.setPositiveButton("Ok", null);
                            dialogBuilder2.show();
                        }
                        if (intent.getStringExtra("Categoria").equals("Flash") && (elapsedHours >2)) {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actividadDrawer.this);
                            dialogBuilder.setMessage("Flash Hatch can last only 2 hours max! Please modify the Event Dates...");
                            dialogBuilder.setPositiveButton("Ok", null);
                            dialogBuilder.show();

                        }
                    } else{
                        AsyncCall task = new AsyncCall();
                        task.execute();
                    }




                } catch (ParseException e) {

                    e.printStackTrace();
                }







            }
        });

        participantes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                    participantes.performClick();
                }

                return false;
            }
        });



        autoCompleteUbicacion=(AutoCompleteTextView)findViewById(R.id.autoCompleteUbicacion);
        GooglePlacesAutocompleteAdapter places = new GooglePlacesAutocompleteAdapter(this,android.R.layout.simple_list_item_1);
        autoCompleteUbicacion.setAdapter(places);



        autoCompleteUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteUbicacion.setText("");
            }
        });

        autoCompleteUbicacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                direccion=autoCompleteUbicacion.getText().toString();
                direccionFinal=direccion.substring(0, direccion.indexOf(','));
                autoCompleteUbicacion.setText(direccionFinal);
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mapFragment.getMapAsync(this);
        setUpMapIfNeeded();


        fecha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    fecha.setText("");

                    new DatePickerDialog(actividadDrawer.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DATE)).show();


                }

                return false;
            }
        });


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }




        buscarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addresses=geocoder.getFromLocationName(direccion,5);
                    if(addresses.size()>0){
                        Double lat=(double)(addresses.get(0).getLatitude());
                        Double lon=(double)(addresses.get(0).getLongitude());
                        latLng=new LatLng(lat,lon);

                        //final LatLng direccionBuscada=new LatLng(lat,lon);
                        Marker direccionUsuario=map.addMarker(new MarkerOptions()
                                .position(latLng));

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                latLng = point;
                map.clear();
                map.addMarker(new MarkerOptions().position(point));

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                autoCompleteUbicacion.setText(addresses.get(0).getAddressLine(0));
            }
        });

        Toast.makeText(this, "Username: "+username, Toast.LENGTH_SHORT).show();

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DATE, dayOfMonth);

            TimePicker timePicker;
            new TimePickerDialog(actividadDrawer.this,time,myCalendar.get(Calendar.HOUR),myCalendar.get(Calendar.MINUTE),false).show();



        }
    };

    DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener(){

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, monthOfYear);
            myCalendarEnd.set(Calendar.DATE, dayOfMonth);

            TimePicker timePicker;
            new TimePickerDialog(actividadDrawer.this,timeEnd,myCalendarEnd.get(Calendar.HOUR),myCalendarEnd.get(Calendar.MINUTE),false).show();

        }

    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR, hourOfDay);
            myCalendar.set(Calendar.MINUTE,minute);

            new DatePickerDialog(actividadDrawer.this, dateEnd, myCalendarEnd
                    .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                    myCalendarEnd.get(Calendar.DATE)).show();

        }
    };

    TimePickerDialog.OnTimeSetListener timeEnd = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendarEnd.set(Calendar.HOUR, hourOfDay);
            myCalendarEnd.set(Calendar.MINUTE,minute);

            updateLabel();
        }
    };


    private void updateLabel() {

        //fecha.setText(myCalendar.get(Calendar.DATE)+"/"+(myCalendar.get(Calendar.MONTH)+1) +"/"+ myCalendar.get(Calendar.YEAR) +" " + myCalendar.get(Calendar.HOUR_OF_DAY) +":"+ myCalendar.get(Calendar.MINUTE) +" - "+ +myCalendarEnd.get(Calendar.DATE)+"/"+(myCalendarEnd.get(Calendar.MONTH)+1) +"/"+ myCalendarEnd.get(Calendar.YEAR) +" " + myCalendarEnd.get(Calendar.HOUR_OF_DAY) +":"+ myCalendarEnd.get(Calendar.MINUTE));

        FechaInicio=myCalendar.getTime();
        FechaFin=myCalendarEnd.getTime();

        String FechaInicioFormateada=formatDateTime(FechaInicio);
        String FechaFinFormateada=formatDateTime(FechaFin);


        fecha.setText(FechaInicioFormateada+" - "+FechaFinFormateada);

        strFechaInicio=getDateTime(FechaInicio);
        strFechaFin=getDateTime(FechaFin);

    }

    private String getDateTime(Date fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return dateFormat.format(fecha);
    }

    private String formatDateTime(Date fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());

        return dateFormat.format(fecha);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
           // map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                //setUpMap();
            }
        }
    }

    private void setUpMap()  {
        map.addMarker(new MarkerOptions().position(latLng).title("Current"));

        try {
            addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

        autoCompleteUbicacion.setText(addresses.get(0).getAddressLine(0));
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actividad_drawer, menu);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if(mLastLocation!=null){
            latLng=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        }

        if(latLng !=null){
            setUpMap();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class AsyncCall extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://nupa.com.ar/insert.php");
                httppost.setEntity(new UrlEncodedFormEntity(parametros));
                HttpResponse response = httpclient.execute(httppost);

            } catch (Exception e) {
                Log.e("log_tag", "Error in Http connection " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Su Hatch fue creado!!!", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getApplicationContext(),MainScreen.class);
            intent.putExtra("username", username);
            intent.putExtra("User_Id", User_Id);
            intent.putExtra("distanceSetting", userDistanceSetting);
            intent.putExtra("categories", userCategoriesSetting);
            startActivity(intent);
            finish();
        }
    }

}
