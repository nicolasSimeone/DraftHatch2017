package nupa.drafthatch;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.opengles.GL10;

public class NuevoHatchStep3 extends AppCompatActivity {


    static ImageView imageViewStep3, HatchImage;
    private Date FechaInicio, FechaFin;
    private Calendar myCalendar = Calendar.getInstance();
    private Calendar myCalendarEnd = Calendar.getInstance();

    private TextView textFechaStart, textFechaEnd;
    private String stringFechaStart, stringFechaEnd;
    private int User_Id;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    static String username;
    private String TitleField, DetailsField;
    private String categorias;
    private String participantes;
    private String Latitud, Longitud, Direccion;
    private String Tipo;
    private double epochStart, epochEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hatch_step3);


        Intent intent=getIntent();

        username= intent.getStringExtra("username");
        userDistanceSetting=intent.getStringExtra("distanceSetting");
        userCategoriesSetting=intent.getStringExtra("categories");

        User_Id=intent.getIntExtra("User_Id", 0);

        participantes=intent.getStringExtra("Participantes");
        categorias=intent.getStringExtra("Categoria");
        TitleField=intent.getStringExtra("Titulo");
        DetailsField=intent.getStringExtra("Detalle");
        Latitud=intent.getStringExtra("Latitud");
        Longitud=intent.getStringExtra("Longitud");
        Direccion=intent.getStringExtra("Direccion");
        Tipo=intent.getStringExtra("Tipo");

        HatchImage=(ImageView)findViewById(R.id.imageViewShow);
        imageViewStep3=(ImageView)findViewById(R.id.imageViewstep3);
        textFechaStart=(TextView)findViewById(R.id.textView);
        textFechaEnd=(TextView)findViewById(R.id.textView7);


        DatePickerDialog dateStartDialog =  new DatePickerDialog(NuevoHatchStep3.this, dateStart, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DATE));
        dateStartDialog.setTitle("Dia de inicio");
        dateStartDialog.show();

    }




    DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener(){

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth - 1) ;

            TimePickerDialog timeStartDialog = new TimePickerDialog(NuevoHatchStep3.this,timeStart,myCalendar.get(Calendar.HOUR),myCalendar.get(Calendar.MINUTE),true);
            timeStartDialog.setTitle("Hora de Inicio");
            timeStartDialog.show();



        }
    };


    TimePickerDialog.OnTimeSetListener timeStart = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR, hourOfDay + 12);
            myCalendar.set(Calendar.MINUTE,minute);

            updateLabelStart();

            DatePickerDialog dateEndDialog = new DatePickerDialog(NuevoHatchStep3.this, dateEnd, myCalendarEnd.get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH), myCalendarEnd.get(Calendar.DATE));
            dateEndDialog.setTitle("Dia de Fin");
            dateEndDialog.show();
        }
    };

    DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener(){

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, monthOfYear);
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth -1);

            TimePickerDialog timeEndDialog = new TimePickerDialog(NuevoHatchStep3.this,timeEnd,myCalendarEnd.get(Calendar.HOUR),myCalendarEnd.get(Calendar.MINUTE),true);
            timeEndDialog.setTitle("Hora de Fin");
            timeEndDialog.show();

        }

    };



    TimePickerDialog.OnTimeSetListener timeEnd = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendarEnd.set(Calendar.HOUR, hourOfDay + 12);
            myCalendarEnd.set(Calendar.MINUTE,minute);

            epochStart = myCalendar.getTimeInMillis();
            epochEnd = myCalendarEnd.getTimeInMillis();

             updateLabelEnd();
            try {
                Intent step4 = new Intent(NuevoHatchStep3.this, NuevoHatchStep4.class);

                step4.putExtra("Titulo", TitleField);
                step4.putExtra("Detalle", DetailsField);
                step4.putExtra("Categoria", categorias);
                step4.putExtra("username", username);
                step4.putExtra("User_Id", User_Id);
                step4.putExtra("categories", userCategoriesSetting);
                step4.putExtra("distanceSetting", userDistanceSetting);
                step4.putExtra("Participantes", participantes);
                step4.putExtra("Tipo", Tipo);

                step4.putExtra("Latitud", Latitud);
                step4.putExtra("Longitud", Longitud);
                step4.putExtra("Direccion", Direccion);

                step4.putExtra("fechaStart", stringFechaStart);
                step4.putExtra("fechaEnd", stringFechaEnd);
                step4.putExtra("epochStart", epochStart);
                step4.putExtra("epochEnd", epochEnd);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date2 = dateFormat.parse(stringFechaEnd);
                Date date1 = dateFormat.parse(stringFechaStart);

                long different = date2.getTime() - date1.getTime();
                long dateEnd = date2.getTime();
                long dateStart = date1.getTime();
                long elapsedHours = TimeUnit.MILLISECONDS.toHours(different);
                long elapsedEnd = TimeUnit.MILLISECONDS.toHours(dateEnd);
                long elapsedStart = TimeUnit.MILLISECONDS.toHours(dateStart);
                if ((elapsedEnd < elapsedStart) || (categorias.equals("Flash") && (elapsedHours > 2))) {
                    if ((elapsedEnd < elapsedStart)) {
                        Toast.makeText(getApplicationContext(), "La fecha de fin debe ser mayor a la de inicio! Por favor modificalas...", Toast.LENGTH_LONG).show();

                        Intent step3 = new Intent(NuevoHatchStep3.this, NuevoHatchStep3.class);

                        step3.putExtra("Titulo", TitleField);
                        step3.putExtra("Detalle", DetailsField);
                        step3.putExtra("Categoria", categorias);
                        step3.putExtra("username", username);
                        step3.putExtra("User_Id", User_Id);
                        step3.putExtra("categories", userCategoriesSetting);
                        step3.putExtra("distanceSetting", userDistanceSetting);
                        step3.putExtra("Participantes", participantes);
                        step3.putExtra("Tipo", Tipo);

                        step3.putExtra("Latitud", Latitud);
                        step3.putExtra("Longitud", Longitud);
                        step3.putExtra("Direccion", Direccion);
                        startActivity(step3);
                        finish();
                    }
                    if (categorias.equals("Flash") && (elapsedHours > 2)) {
                        Toast.makeText(getApplicationContext(), "Los eventos Flashs solo pueden durar dos horas, por favor modifica las fechas", Toast.LENGTH_LONG).show();
                        Intent step3 = new Intent(NuevoHatchStep3.this, NuevoHatchStep3.class);

                        step3.putExtra("Titulo", TitleField);
                        step3.putExtra("Detalle", DetailsField);
                        step3.putExtra("Categoria", categorias);
                        step3.putExtra("username", username);
                        step3.putExtra("User_Id", User_Id);
                        step3.putExtra("categories", userCategoriesSetting);
                        step3.putExtra("distanceSetting", userDistanceSetting);
                        step3.putExtra("Participantes", participantes);
                        step3.putExtra("Tipo", Tipo);

                        step3.putExtra("Latitud", Latitud);
                        step3.putExtra("Longitud", Longitud);
                        step3.putExtra("Direccion", Direccion);
                        startActivity(step3);
                        finish();
                    }
                } else {
                    startActivity(step4);
                    //finish();
                }
            }catch(ParseException p){

            }
        }
    };


    private void updateLabelStart() {

        FechaInicio=myCalendar.getTime();
        stringFechaStart=getDateTime(FechaInicio);

    }

    private void updateLabelEnd() {

        FechaFin=myCalendarEnd.getTime();

        stringFechaEnd=getDateTime(FechaFin);

    }

    private String getDateTime(Date fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());

        return dateFormat.format(fecha);
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

        myCalendar = Calendar.getInstance();
        myCalendarEnd = Calendar.getInstance();

        DatePickerDialog dateStartDialog =  new DatePickerDialog(NuevoHatchStep3.this, dateStart, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DATE));
        dateStartDialog.setTitle("Dia de inicio");
        dateStartDialog.show();
    }

}




